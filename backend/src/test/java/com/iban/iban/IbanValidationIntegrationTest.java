package com.iban.iban;

import com.iban.AbstractIntegrationTest;
import com.iban.bank.Bank;
import com.iban.bank.BankRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class IbanValidationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankRepository bankRepository;

    @Test
    void validateGermanIban_bankExists_returnsValid() throws Exception {
        bankRepository.save(new Bank("37040044", "DE", "Commerzbank", null));

        mockMvc.perform(get("/api/v1/iban/validation")
                .param("iban", "DE89370400440532013000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.bankName").value("Commerzbank"));
    }

    @Test
    void validateGermanIban_bankNotFound_returnsValidWithWarning() throws Exception {
        mockMvc.perform(get("/api/v1/iban/validation")
                .param("iban", "DE89370400440532013000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.warning").value("Bank not found"));
    }

    @Test
    void validateInvalidIban_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/iban/validation")
                .param("iban", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void validateNonGermanIban_returnsValidWithWarning() throws Exception {
        mockMvc.perform(get("/api/v1/iban/validation")
                .param("iban", "GB82WEST12345698765432"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.warning").isNotEmpty());
    }
}
