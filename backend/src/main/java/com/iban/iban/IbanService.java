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
                return IbanValidationResponse.validated(iban.getValue(), null, AccountValidationResult.NOT_IMPLEMENTED);
            }

            Optional<Bank> bankOpt = bankService.getBankByIban(iban);
            if (bankOpt.isEmpty()) {
                return IbanValidationResponse.validated(iban.getValue(), null, AccountValidationResult.NOT_IMPLEMENTED);
            }

            Bank bank = bankOpt.get();
            AccountValidationResult accountResult = bank.accountValidation(iban.extractBankAccount());

            log.info("IBAN validated: {} bank={} account={}", value, bank.getName(), accountResult);
            return IbanValidationResponse.validated(iban.getValue(), bank.getName(), accountResult);
        } catch (IllegalArgumentException e) {
            log.error("IBAN validation error: {}", e.getMessage());
            return IbanValidationResponse.patternInvalid(e.getMessage());
        }
    }

    private Iban getIban(String value) {
        String countryCode = Iban.extractCountryCode(value);
        return switch (countryCode) {
            case "DE" -> new GermanIban(value);
            case "AT" -> new AustriaIban(value);
            default -> new BasicIban(value);
        };
    }
}
