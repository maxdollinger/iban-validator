package com.iban.iban;

import com.iban.iban.Iban.*;
import org.springframework.stereotype.Service;

@Service
public class IbanService {

    public IbanService() {
    }

    public IbanValidationResponse validate(String value) {
        try {
            Iban iban = getIban(value);

            if (iban instanceof BasicIban) {
                String warning = "No special validation for " + iban.countryCode()
                        + "implemented. Could still be invalid.";
                return IbanValidationResponse.success(iban.getValue(), warning);
            }
            return IbanValidationResponse.success(iban.getValue(), null);
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
