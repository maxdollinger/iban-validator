package com.iban.iban.Iban;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GermanIbanTest {

    @Test
    void validIban_parsesSuccessfully() {
        GermanIban iban = new GermanIban("DE89370400440532013000");
        assertEquals("DE", iban.countryCode());
        assertEquals("DE89370400440532013000", iban.getValue());
    }

    @Test
    void extractBankCode_returnsFirst8CharsOfBban() {
        GermanIban iban = new GermanIban("DE89370400440532013000");
        assertEquals("37040044", iban.extractBankCode());
    }

    @Test
    void extractBankAccount_returnsRemainingBban() {
        GermanIban iban = new GermanIban("DE89370400440532013000");
        assertEquals("0532013000", iban.extractBankAccount());
    }

    @Test
    void invalidPattern_throws() {
        assertThrows(IllegalArgumentException.class, () -> new GermanIban("XX89370400440532013000"));
    }

    @Test
    void invalidChecksum_throws() {
        assertThrows(IllegalArgumentException.class, () -> new GermanIban("DE00370400440532013000"));
    }

    @Test
    void normalizesInput() {
        GermanIban iban = new GermanIban("de89 3704 0044 0532 0130 00");
        assertEquals("DE89370400440532013000", iban.getValue());
    }
}
