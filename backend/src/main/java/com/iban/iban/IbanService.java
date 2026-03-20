package com.iban.iban;

import com.iban.iban.model.Iban;
import com.iban.iban.validation.CountryIbanValidator;
import com.iban.iban.validation.CountryIbanValidatorRegistry;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IbanService {

    private final CountryIbanValidatorRegistry registry;

    public IbanService(CountryIbanValidatorRegistry registry) {
        this.registry = registry;
    }

    public IbanValidationResponse validate(String value) {
        try {
            Iban iban = Iban.fromString(value);

            Optional<CountryIbanValidator> validator = registry.find(iban.getCountryCode());

            if (validator.isEmpty()) {
                String warning = "No country-specific validation available for "
                        + iban.getCountryCode() + ". The IBAN may still be invalid.";
                return IbanValidationResponse.success(
                        iban.iban, iban.getCountryCode(), iban.getBankCode(), iban.getBankAccount(), warning);
            }

            Optional<String> bankCodeError = validator.get().validateBankCode(iban);
            if (bankCodeError.isPresent()) return IbanValidationResponse.failure(bankCodeError.get());

            Optional<String> accountError = validator.get().validateBankAccount(iban);
            if (accountError.isPresent()) return IbanValidationResponse.failure(accountError.get());

            return IbanValidationResponse.success(
                    iban.iban, iban.getCountryCode(), iban.getBankCode(), iban.getBankAccount(), null);

        } catch (IllegalArgumentException e) {
            return IbanValidationResponse.failure(e.getMessage());
        }
    }
}
