package com.iban.bank;

import com.iban.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class BankIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankRepository bankRepository;

    @Test
    void upsertBank_createsNewBank() throws Exception {
        mockMvc.perform(post("/api/v1/bank/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "bankCode": "37040044",
                        "countryCode": "DE",
                        "name": "Commerzbank",
                        "accountAlgo": null
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankCode").value("37040044"))
                .andExpect(jsonPath("$.countryCode").value("DE"))
                .andExpect(jsonPath("$.name").value("Commerzbank"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void upsertBank_updatesExistingBank() throws Exception {
        bankRepository.save(new Bank("37040044", "DE", "Commerzbank OLD", null));

        mockMvc.perform(post("/api/v1/bank/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "bankCode": "37040044",
                        "countryCode": "DE",
                        "name": "Commerzbank AG",
                        "accountAlgo": null
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Commerzbank AG"));
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
