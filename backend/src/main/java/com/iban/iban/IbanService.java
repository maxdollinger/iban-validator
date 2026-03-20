package com.iban.iban;

import com.iban.bank.Bank;
import com.iban.bank.BankService;
import com.iban.iban.Iban.*;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IbanService {

    private static final Logger log = LoggerFactory.getLogger(IbanService.class);

    private BankService bankService;

    public IbanService(BankService bankService) {
        this.bankService = bankService;
    }

    public IbanValidationResponse validate(String value) {
        log.info("Validating IBAN: {}", value);
        try {
            Iban iban = getIban(value);

            if (iban instanceof BasicIban) {
                log.warn("No specific validation for country={}", iban.countryCode());
                String warning = "No special validation for " + iban.countryCode()
                        + "implemented. Could still be invalid.";
                return IbanValidationResponse.success(iban.getValue(), null, warning);
            }

            Optional<Bank> bankOpt = bankService.getBankByIban(iban);
            if (bankOpt.isEmpty()) {
                return IbanValidationResponse.success(iban.getValue(), null, "Bank not found");
            }

            Bank bank = bankOpt.get();

            if (!bank.accountValidation(iban.extractBankAccount())) {
                log.warn("Account validation failed for IBAN: {}", value);
                return IbanValidationResponse.failure("IBAN has a invalid bank account");
            }

            log.info("IBAN valid: {} bank={}", value, bank.getName());
            return IbanValidationResponse.success(iban.getValue(), bank.getName(), null);
        } catch (IllegalArgumentException e) {
            log.error("IBAN validation error: {}", e.getMessage());
            return IbanValidationResponse.failure(e.getMessage());
        }
    }

    private Iban getIban(String value) {
        String countryCode = Iban.extractCountryCode(value);
        return switch (countryCode) {
            case "DE" -> new GermanIban(value);
            default -> new BasicIban(value);
        };
    }
}
