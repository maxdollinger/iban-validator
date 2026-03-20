package com.iban.iban;

import com.iban.bank.Bank;
import com.iban.bank.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IbanServiceTest {

    @Mock
    private BankService bankService;

    private IbanService service;

    @BeforeEach
    void setUp() {
        service = new IbanService(bankService);
    }

    @Test
    void validGermanIban_returnsSuccess() {
        Bank bank = new Bank("37040044", "DE", "Commerzbank", null);
        when(bankService.getBankByIban(any())).thenReturn(Optional.of(bank));

        IbanValidationResponse response = service.validate("DE89370400440532013000");

        assertTrue(response.valid());
        assertEquals("DE89370400440532013000", response.iban());
        assertNull(response.error());
        assertNull(response.warning());
    }

    @Test
    void validGermanIban_bankNotFound_returnsSuccessWithWarning() {
        when(bankService.getBankByIban(any())).thenReturn(Optional.empty());

        IbanValidationResponse response = service.validate("DE89370400440532013000");

        assertTrue(response.valid());
        assertEquals("DE89370400440532013000", response.iban());
        assertEquals("Bank not found", response.warning());
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
