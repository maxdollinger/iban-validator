package com.iban.iban.Iban;

public class AustriaIban extends Iban {

    private static final String PATTERN = "AT[0-9]{2}[0-9]{16}";
    private static final int BANK_CODE_LENGTH = 5;

    public AustriaIban(String value) {
        super(value, PATTERN);
    }

    public String countryCode() {
        return "AT";
    }

    public String extractBankCode() {
        return bban.substring(0, BANK_CODE_LENGTH);
    }

    public String extractBankAccount() {
        return bban.substring(BANK_CODE_LENGTH);
    }
}
