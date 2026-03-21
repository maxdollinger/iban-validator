package com.iban.bank;

import com.iban.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

class BankIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BankRepository bankRepository;

    @BeforeEach
    void setUp() {
        bankRepository.deleteAll();
    }

    @Test
    void upsertBank_createsNewBank() {
        webTestClient.post().uri("/api/v1/bank/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "bankCode": "37040044",
                        "countryCode": "DE",
                        "name": "Commerzbank",
                        "accountAlgo": null
                    }
                    """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bankCode").isEqualTo("37040044")
                .jsonPath("$.countryCode").isEqualTo("DE")
                .jsonPath("$.name").isEqualTo("Commerzbank")
                .jsonPath("$.id").isNumber();
    }

    @Test
    void upsertBank_updatesExistingBank() {
        bankRepository.save(new Bank("37040044", "DE", "Commerzbank OLD", null));

        webTestClient.post().uri("/api/v1/bank/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "bankCode": "37040044",
                        "countryCode": "DE",
                        "name": "Commerzbank AG",
                        "accountAlgo": null
                    }
                    """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Commerzbank AG");
    }

    @Test
    void repositoryQueries_returnCorrectResults() {
        bankRepository.save(new Bank("37040044", "DE", "Commerzbank", null));
        bankRepository.save(new Bank("10010010", "DE", "Postbank", null));

        assertThat(bankRepository.findByBankCode("37040044")).isPresent();
        assertThat(bankRepository.findByCountryCode("DE")).hasSize(2);
        assertThat(bankRepository.findByCountryCodeAndBankCode("DE", "37040044")).isPresent();
        assertThat(bankRepository.findByCountryCodeAndBankCode("DE", "99999999")).isEmpty();
    }
}
