package com.iban.iban.Iban;

public class GermanIban extends Iban {

    private static final String PATTERN = "DE[0-9]{2}[0-9]{18}";
    private static final int BANK_CODE_LENGTH = 8;

    public GermanIban(String value) {
        super(value, PATTERN);
    }

    public String countryCode() {
        return "DE";
    }

    public String extractBankCode() {
        return bban.substring(0, BANK_CODE_LENGTH);
    }

    public String extractBankAccount() {
        return bban.substring(BANK_CODE_LENGTH);
    }
}
