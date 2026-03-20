package com.iban.iban;

public record IbanValidationResponse(
        boolean valid,
        String iban,
        String warning,
        String error) {

    public static IbanValidationResponse success(
            String iban,
            String warning) {
        return new IbanValidationResponse(true, iban, warning, null);
    }

    public static IbanValidationResponse failure(String error) {
        return new IbanValidationResponse(false, null, null, error);
    }
}
