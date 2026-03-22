package com.iban.common;

import java.util.Locale;
import java.util.Set;

public final class CountryCodeUtil {

    private static final Set<String> ISO_COUNTRIES = Set.of(Locale.getISOCountries());

    private CountryCodeUtil() {
    }

    public static String validate(String countryCode) {
        if (countryCode == null || countryCode.length() != 2) {
            throw new IllegalArgumentException("Country code must be exactly 2 letters");
        }

        String normalized = countryCode.toUpperCase();

        if (!ISO_COUNTRIES.contains(normalized)) {
            throw new IllegalArgumentException("No valid country code found");
        }

        return normalized;
    }
}
