package com.iban.iban.validation;

import com.iban.iban.model.Iban;

import java.util.Optional;

public interface CountryIbanValidator {
    String supportedCountryCode();
    Optional<String> validateBankCode(Iban iban);
    Optional<String> validateBankAccount(Iban iban);
}
