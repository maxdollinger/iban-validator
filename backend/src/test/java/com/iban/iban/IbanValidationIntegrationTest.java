package com.iban.iban;

import com.iban.AbstractIntegrationTest;
import com.iban.bank.Bank;
import com.iban.bank.BankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

class IbanValidationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BankRepository bankRepository;

    @BeforeEach
    void setUp() {
        bankRepository.deleteAll();
    }

    @Test
    void validateGermanIban_bankExists_returnsValid() {
        bankRepository.save(new Bank("37040044", "DE", "Commerzbank", null));

        webTestClient.get().uri("/api/v1/iban/validation?iban=DE89370400440532013000")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.patternValid").isEqualTo(true)
                .jsonPath("$.bankName").isEqualTo("Commerzbank")
                .jsonPath("$.accountNumberValidation").isEqualTo("NOT_IMPLEMENTED");
    }

    @Test
    void validateGermanIban_bankNotFound_returnsValidWithoutBank() {
        webTestClient.get().uri("/api/v1/iban/validation?iban=DE89370400440532013000")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.patternValid").isEqualTo(true)
                .jsonPath("$.bankName").doesNotExist()
                .jsonPath("$.accountNumberValidation").isEqualTo("NOT_IMPLEMENTED");
    }

    @Test
    void validateInvalidIban_returnsBadRequest() {
        webTestClient.get().uri("/api/v1/iban/validation?iban=INVALID")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.patternValid").isEqualTo(false)
                .jsonPath("$.error").isNotEmpty();
    }

    @Test
    void validateNonGermanIban_returnsValidNotImplemented() {
        webTestClient.get().uri("/api/v1/iban/validation?iban=GB82WEST12345698765432")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.patternValid").isEqualTo(true)
                .jsonPath("$.accountNumberValidation").isEqualTo("NOT_IMPLEMENTED");
    }
}
