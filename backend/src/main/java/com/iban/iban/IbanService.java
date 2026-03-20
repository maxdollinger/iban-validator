package com.iban.iban;

import com.iban.bank.Bank;
import com.iban.bank.BankService;
import com.iban.iban.Iban.*;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class IbanService {

    private BankService bankService;

    public IbanService(BankService bankService) {
        this.bankService = bankService;
    }

    public IbanValidationResponse validate(String value) {
        try {
            Iban iban = getIban(value);

            if (iban instanceof BasicIban) {
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
                return IbanValidationResponse.failure("IBAN has a invalid bank account");
            }

            return IbanValidationResponse.success(iban.getValue(), bank.getName(), null);
        } catch (IllegalArgumentException e) {
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
