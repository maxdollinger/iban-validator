package com.iban.bank;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface BankRepository extends JpaRepository<Bank, Long> {

    Optional<Bank> findByBankCode(String bankCode);

    List<Bank> findByCountryCode(String countryCode);

    Optional<Bank> findByCountryCodeAndBankCode(String countryCode, String bankCode);
}
