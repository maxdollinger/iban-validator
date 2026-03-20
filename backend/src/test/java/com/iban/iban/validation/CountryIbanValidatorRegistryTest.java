package com.iban.iban.validation;

import com.iban.iban.validation.de.DeCountryIbanValidator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CountryIbanValidatorRegistryTest {

    private final CountryIbanValidatorRegistry registry = new CountryIbanValidatorRegistry(
            List.of(new DeCountryIbanValidator(bankCode -> true))
    );

    @Test
    void find_registeredCountryCode_returnsValidator() {
        Optional<CountryIbanValidator> result = registry.find("DE");

        assertThat(result).isPresent();
        assertThat(result.get().supportedCountryCode()).isEqualTo("DE");
    }

    @Test
    void find_unregisteredCountryCode_returnsEmpty() {
        Optional<CountryIbanValidator> result = registry.find("GB");

        assertThat(result).isEmpty();
    }
}
