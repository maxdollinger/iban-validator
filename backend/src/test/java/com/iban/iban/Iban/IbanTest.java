package com.iban.iban.Iban;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IbanTest {

    @Test
    void normalizeIban_stripsWhitespaceAndUppercases() {
        assertEquals("DE89370400440532013000", Iban.normalizeIban("de89 3704 0044 0532 0130 00"));
    }

    @Test
    void normalizeIban_stripsDashes() {
        assertEquals("DE89370400440532013000", Iban.normalizeIban("DE89-3704-0044-0532-0130-00"));
    }

    @Test
    void isChecksumValid_validIban() {
        assertTrue(Iban.isChecksumValid("DE89370400440532013000"));
    }

    @Test
    void isChecksumValid_invalidIban() {
        assertFalse(Iban.isChecksumValid("DE00370400440532013000"));
    }

    @Test
    void extractCountryCode_validCode() {
        assertEquals("DE", Iban.extractCountryCode("DE89"));
    }

    @Test
    void extractCountryCode_invalidCode_throws() {
        assertThrows(IllegalArgumentException.class, () -> Iban.extractCountryCode("XX"));
    }

    @Test
    void extractCountryCode_tooShort_throws() {
        assertThrows(IllegalArgumentException.class, () -> Iban.extractCountryCode("D"));
    }
}
