package com.iban.iban.validation.de;

import com.iban.bank.BankCodeValidator;
import com.iban.iban.model.Iban;
import com.iban.iban.validation.CountryIbanValidator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DeCountryIbanValidator implements CountryIbanValidator {

    private final BankCodeValidator bankCodeValidator;

    public DeCountryIbanValidator(BankCodeValidator bankCodeValidator) {
        this.bankCodeValidator = bankCodeValidator;
    }

    public String supportedCountryCode() {
        return "DE";
    }

    public Optional<String> validateBankCode(Iban iban) {
        return bankCodeValidator.isKnown(iban.getBankCode())
                ? Optional.empty()
                : Optional.of("Bank code " + iban.getBankCode() + " is not registered");
    }

    public Optional<String> validateBankAccount(Iban iban) {
        return Optional.empty();
    }
}
