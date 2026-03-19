package com.iban.iban;

import java.math.BigInteger;
import java.util.Locale;

public abstract class Iban {

    private static final String VALIDATION_PATTERN = "[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}";

    public final String bban;
    public final String iban;

    protected Iban(String iban) {
        this.bban = iban.substring(0, 4);
        this.iban = iban;
    }

    public abstract String getBankCode();

    public abstract String getBankAccount();

    public final String getCountryCode() {
        return iban.substring(0, 2);
    }

    public static Iban fromString(String value) {
        String normalizedIban = value.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();

        if (!normalizedIban.matches(VALIDATION_PATTERN)) {
            throw new IllegalArgumentException("IBAN has invalid pattern");
        }

        String countryCode = normalizedIban.substring(0, 2);
        if (!validateCountryCode(countryCode)) {
            throw new IllegalArgumentException("IBAN has no valid country code");
        }

        if (mod97(normalizedIban) != 1) {
            throw new IllegalArgumentException("IBAN has no valid checksum");
        }

        return switch (countryCode) {
            case "DE" -> new DeIban(normalizedIban);
            default -> new DefaultIban(normalizedIban);
        };
    }

    private static boolean validateCountryCode(String countryCode) {

        for (String isoCode : Locale.getISOCountries()) {
            if (isoCode.equalsIgnoreCase(countryCode)) {
                return true;
            }
        }

        return false;
    }

    private static int mod97(String normalizedIban) {
        String rearranged = normalizedIban.substring(4) + normalizedIban.substring(0, 4);

        StringBuilder numeric = new StringBuilder();
        for (char ch : rearranged.toCharArray()) {
            if (Character.isLetter(ch)) {
                numeric.append(ch - 'A' + 10);
            } else {
                numeric.append(ch);
            }
        }

        return new BigInteger(numeric.toString()).mod(BigInteger.valueOf(97)).intValue();
    }

    @Override
    public String toString() {
        return iban;
    }
}
