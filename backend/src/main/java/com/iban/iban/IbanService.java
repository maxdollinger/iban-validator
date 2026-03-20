package com.iban.iban;

import com.iban.iban.model.DefaultIban;
import com.iban.iban.model.Iban;
import org.springframework.stereotype.Service;

@Service
public class IbanService {

    public IbanValidationResponse validate(String value) {
        try {
            Iban iban = Iban.fromString(value);

            String warning = iban instanceof DefaultIban
                    ? "No country-specific validation available for " + iban.getCountryCode() + ". The IBAN may still be invalid."
                    : null;

            return IbanValidationResponse.success(
                    iban.iban,
                    iban.getCountryCode(),
                    iban.getBankCode(),
                    iban.getBankAccount(),
                    warning
            );
        } catch (IllegalArgumentException e) {
            return IbanValidationResponse.failure(e.getMessage());
        }
    }
}
