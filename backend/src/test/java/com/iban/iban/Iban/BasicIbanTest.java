package com.iban.iban.Iban;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicIbanTest {

    @Test
    void validGbIban_parsesSuccessfully() {
        BasicIban iban = new BasicIban("GB82WEST12345698765432");
        assertEquals("GB", iban.countryCode());
        assertEquals("GB82WEST12345698765432", iban.getValue());
    }

    @Test
    void extractBankCode_returnsEmpty() {
        BasicIban iban = new BasicIban("GB82WEST12345698765432");
        assertEquals("", iban.extractBankCode());
    }

    @Test
    void extractBankAccount_returnsEmpty() {
        BasicIban iban = new BasicIban("GB82WEST12345698765432");
        assertEquals("", iban.extractBankAccount());
    }

    @Test
    void invalidPattern_throws() {
        assertThrows(IllegalArgumentException.class, () -> new BasicIban("123"));
    }

    @Test
    void invalidChecksum_throws() {
        assertThrows(IllegalArgumentException.class, () -> new BasicIban("GB00WEST12345698765432"));
    }
}
