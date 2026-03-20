package com.iban.iban;

public record IbanValidationResponse(
        boolean valid,
        String iban,
        String countryCode,
        String bankCode,
        String bankAccount,
        String warning,
        String error
) {
    public static IbanValidationResponse success(
            String iban,
            String countryCode,
            String bankCode,
            String bankAccount,
            String warning
    ) {
        return new IbanValidationResponse(true, iban, countryCode, bankCode, bankAccount, warning, null);
    }

    public static IbanValidationResponse failure(String error) {
        return new IbanValidationResponse(false, null, null, null, null, null, error);
    }
}
