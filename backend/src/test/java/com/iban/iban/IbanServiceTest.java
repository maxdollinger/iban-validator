package com.iban.iban;

import com.iban.bank.BankCodeValidator;
import com.iban.iban.validation.CountryIbanValidatorRegistry;
import com.iban.iban.validation.de.DeCountryIbanValidator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IbanServiceTest {

    private IbanService serviceWithBankCodeValidator(BankCodeValidator bankCodeValidator) {
        DeCountryIbanValidator deValidator = new DeCountryIbanValidator(bankCodeValidator);
        CountryIbanValidatorRegistry registry = new CountryIbanValidatorRegistry(List.of(deValidator));
        return new IbanService(registry);
    }

    private final IbanService service = serviceWithBankCodeValidator(bankCode -> true);

    @Test
    void validate_validGermanIban_returnsSuccess() {
        IbanValidationResponse response = service.validate("DE89370400440532013000");

        assertThat(response.valid()).isTrue();
        assertThat(response.iban()).isEqualTo("DE89370400440532013000");
        assertThat(response.countryCode()).isEqualTo("DE");
        assertThat(response.bankCode()).isEqualTo("37040044");
        assertThat(response.bankAccount()).isEqualTo("0532013000");
        assertThat(response.warning()).isNull();
        assertThat(response.error()).isNull();
    }

    @Test
    void validate_validNonGermanIban_returnsSuccessWithWarning() {
        IbanValidationResponse response = service.validate("GB29NWBK60161331926819");

        assertThat(response.valid()).isTrue();
        assertThat(response.warning()).contains("GB");
        assertThat(response.error()).isNull();
    }

    @Test
    void validate_invalidIban_returnsFailure() {
        IbanValidationResponse response = service.validate("INVALID");

        assertThat(response.valid()).isFalse();
        assertThat(response.error()).isNotNull();
        assertThat(response.iban()).isNull();
    }

    @Test
    void validate_germanIbanWithUnknownBankCode_returnsFailure() {
        IbanService serviceWithStrictValidator = serviceWithBankCodeValidator(bankCode -> false);

        IbanValidationResponse response = serviceWithStrictValidator.validate("DE89370400440532013000");

        assertThat(response.valid()).isFalse();
        assertThat(response.error()).contains("37040044");
        assertThat(response.error()).contains("not registered");
    }
}
