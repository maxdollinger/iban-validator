package com.iban.iban;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IbanServiceTest {

    private IbanService service;

    @BeforeEach
    void setUp() {
        service = new IbanService();
    }

    @Test
    void validGermanIban_returnsSuccess() {
        IbanValidationResponse response = service.validate("DE89370400440532013000");

        assertTrue(response.valid());
        assertEquals("DE89370400440532013000", response.iban());
        assertNull(response.error());
        assertNull(response.warning());
    }

    @Test
    void invalidGermanIban_returnsFailure() {
        IbanValidationResponse response = service.validate("DE00000000000000000000");

        assertFalse(response.valid());
        assertNull(response.iban());
        assertNotNull(response.error());
    }

    @Test
    void validNonGermanIban_returnsSuccessWithWarning() {
        IbanValidationResponse response = service.validate("GB82WEST12345698765432");

        assertTrue(response.valid());
        assertNotNull(response.warning());
        assertNull(response.error());
    }

    @Test
    void invalidIban_returnsFailure() {
        IbanValidationResponse response = service.validate("INVALID");

        assertFalse(response.valid());
        assertNotNull(response.error());
    }
}
