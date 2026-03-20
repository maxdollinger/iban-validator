package com.iban.iban.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IbanTest {

    @Test
    void fromString_tooShort_throwsInvalidPattern() {
        assertThatThrownBy(() -> Iban.fromString("DE89370"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("pattern");
    }

    @Test
    void fromString_tooLong_throwsInvalidPattern() {
        // BBAN part must be at most 30 chars; 31 'A's pushes it over the limit
        String tooLong = "FR89" + "A".repeat(31);
        assertThatThrownBy(() -> Iban.fromString(tooLong))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("pattern");
    }

    @Test
    void fromString_invalidCountryCode_throwsInvalidCountryCode() {
        // "XX" satisfies the regex pattern but is not a valid ISO country code
        assertThatThrownBy(() -> Iban.fromString("XX760700240340001234567800"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("country code");
    }

    @Test
    void fromString_validGermanIban_returnsDeIban() {
        Iban iban = Iban.fromString("DE89370400440532013000");

        assertThat(iban).isInstanceOf(DeIban.class);
        assertThat(iban.getCountryCode()).isEqualTo("DE");
        assertThat(iban.iban).isEqualTo("DE89370400440532013000");
    }

}
