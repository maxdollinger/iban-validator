package com.iban.iban.Iban;

public class BasicIban extends Iban {

    private static final String PATTERN = "[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}";

    public BasicIban(String value) {
        super(value, PATTERN);
    }

    public String countryCode() {
        return iban.substring(0, 2);
    }

    public String extractBankCode() {
        return "";
    }

    public String extractBankAccount() {
        return "";
    }
}
