package com.iban.iban;

public record IbanValidationResponse(
        boolean valid,
        String iban,
        String bankName,
        String warning,
        String error) {

    public static IbanValidationResponse success(
            String iban,
            String bankName,
            String warning) {
        return new IbanValidationResponse(true, iban, bankName, warning, null);
    }

    public static IbanValidationResponse failure(String error) {
        return new IbanValidationResponse(false, null, null, null, error);
    }
}
