package com.iban.iban.validation;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class CountryIbanValidatorRegistry {

    private final Map<String, CountryIbanValidator> validators;

    public CountryIbanValidatorRegistry(List<CountryIbanValidator> validators) {
        this.validators = validators.stream()
                .collect(toMap(CountryIbanValidator::supportedCountryCode, identity()));
    }

    public Optional<CountryIbanValidator> find(String countryCode) {
        return Optional.ofNullable(validators.get(countryCode));
    }
}
