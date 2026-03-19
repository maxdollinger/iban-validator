package com.iban.iban;

public class DeIban extends Iban {

    public static final String VALIDATION_PATTERN = "DE[0-9]{2}[0-9]{18}";
    private static final int BANK_CODE_LENGTH = 8;

    public DeIban(String iban) {
        super(iban);
        if (!iban.matches(VALIDATION_PATTERN)) {
            throw new IllegalArgumentException("IBAN has invalid german pattern");
        }
    }

    public String getBankCode() {
        return bban.substring(0, BANK_CODE_LENGTH);
    }

    public String getBankAccount() {
        return bban.substring(BANK_CODE_LENGTH);
    }
}
