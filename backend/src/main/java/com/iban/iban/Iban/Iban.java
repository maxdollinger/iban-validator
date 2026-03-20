package com.iban.iban.Iban;

import java.util.Locale;

public abstract class Iban {

    protected final String iban;
    protected final String bban;

    protected Iban(String value, String pattern) {
        String normalizedIban = normalizeIban(value);
        if (!normalizedIban.matches(pattern)) {
            throw new IllegalArgumentException("IBAN pattern invalid");
        }

        if (!isChecksumValid(normalizedIban)) {
            throw new IllegalArgumentException("IBAN with invalid checksum");
        }

        this.iban = normalizedIban;
        this.bban = normalizedIban.substring(4);
    }

    public abstract String countryCode();

    public abstract String extractBankCode();

    public abstract String extractBankAccount();

    public String getValue() {
        return iban;
    }

    public static boolean isChecksumValid(String iban) {
        String rearranged = iban.substring(4) + iban.substring(0, 4);

        StringBuilder numeric = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) {
                numeric.append(c - 'A' + 10);
            } else {
                numeric.append(c);
            }
        }

        String numericStr = numeric.toString();
        int remainder = 0;
        for (int i = 0; i < numericStr.length(); i++) {
            remainder = (remainder * 10 + (numericStr.charAt(i) - '0')) % 97;
        }

        return remainder == 1;
    }

    public static String normalizeIban(String value) {
        return value.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    public static String extractCountryCode(String value) {
        if (value.length() < 2) {
            throw new IllegalArgumentException("Value must have at least 2 letters for a valid country code");
        }

        String countryCode = value.substring(0, 2).toUpperCase();

        for (String code : Locale.getISOCountries()) {
            if (code == countryCode) {
                return countryCode;
            }
        }

        throw new IllegalArgumentException("No valid country code found");
    }
}
