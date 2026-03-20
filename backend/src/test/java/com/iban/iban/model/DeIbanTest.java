package com.iban.iban.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeIbanTest {

    private final DeIban iban = (DeIban) Iban.fromString("DE89370400440532013000");

    @Test
    void getBankCode_returnsFirst8CharsOfBban() {
        assertThat(iban.getBankCode()).isEqualTo("37040044");
    }

    @Test
    void getBankAccount_returnsLast10CharsOfBban() {
        assertThat(iban.getBankAccount()).isEqualTo("0532013000");
    }
}
