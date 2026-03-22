package com.iban.iban;

public record IbanValidationResponse(
        String iban,
        boolean patternValid,
        String bankName,
        AccountValidationResult accountNumberValidation,
        String error) {

    public static IbanValidationResponse patternInvalid(String error) {
        return new IbanValidationResponse(null, false, null, null, error);
    }

    public static IbanValidationResponse validated(
            String iban,
            String bankName,
            AccountValidationResult accountNumberValidation) {
        return new IbanValidationResponse(iban, true, bankName, accountNumberValidation, null);
    }
}
