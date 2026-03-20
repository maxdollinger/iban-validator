package com.iban.iban.validation.de;

import com.iban.iban.model.Iban;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DeCountryIbanValidatorTest {

    private static final Iban VALID_DE_IBAN = Iban.fromString("DE89370400440532013000");

    @Test
    void validateBankCode_knownBankCode_returnsEmpty() {
        DeCountryIbanValidator validator = new DeCountryIbanValidator(bankCode -> true);

        Optional<String> result = validator.validateBankCode(VALID_DE_IBAN);

        assertThat(result).isEmpty();
    }

    @Test
    void validateBankCode_unknownBankCode_returnsErrorMessage() {
        DeCountryIbanValidator validator = new DeCountryIbanValidator(bankCode -> false);

        Optional<String> result = validator.validateBankCode(VALID_DE_IBAN);

        assertThat(result).isPresent();
        assertThat(result.get()).contains("37040044");
        assertThat(result.get()).contains("not registered");
    }

    @Test
    void validateBankAccount_alwaysReturnsEmpty() {
        DeCountryIbanValidator validator = new DeCountryIbanValidator(bankCode -> true);

        Optional<String> result = validator.validateBankAccount(VALID_DE_IBAN);

        assertThat(result).isEmpty();
    }

    @Test
    void supportedCountryCode_returnsDE() {
        DeCountryIbanValidator validator = new DeCountryIbanValidator(bankCode -> true);

        assertThat(validator.supportedCountryCode()).isEqualTo("DE");
    }
}
