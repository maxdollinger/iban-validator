package com.iban.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iban.common.CountryCodeUtil;
import com.iban.iban.Iban.Iban;

import java.util.Optional;

@Service
public class BankService {

    private static final Logger log = LoggerFactory.getLogger(BankService.class);

    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public Optional<Bank> getBankByIban(Iban iban) {
        Optional<Bank> bank = bankRepository.findByCountryCodeAndBankCode(iban.countryCode(),
                iban.extractBankCode());
        if (bank.isEmpty()) {
            log.warn("Bank not found for country={} bankCode={}", iban.countryCode(), iban.extractBankCode());
        }
        return bank;
    }

    public Bank upsertBank(String countryCode, String bankCode, String name, String accountAlgo) {
        String validatedCountryCode = CountryCodeUtil.validate(countryCode);
        requireAlphanumeric(bankCode, "bankCode");
        requireAlphanumeric(name, "name");
        if (accountAlgo != null && !accountAlgo.isBlank()) {
            requireAlphanumeric(accountAlgo, "accountAlgo");
        }

        boolean exists = bankRepository.findByCountryCodeAndBankCode(validatedCountryCode, bankCode).isPresent();
        Bank bank = bankRepository.findByCountryCodeAndBankCode(validatedCountryCode, bankCode)
                .orElse(new Bank(bankCode, validatedCountryCode, name, accountAlgo));

        bank.setName(name);
        bank.setAccountAlgo(accountAlgo);

        Bank saved = bankRepository.save(bank);
        log.info("{} bank: country={} bankCode={} name={}", exists ? "Updated" : "Created", validatedCountryCode,
                bankCode, name);
        return saved;
    }

    private void requireAlphanumeric(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        if (!value.matches("[a-zA-Z0-9\\s-]+")) {
            throw new IllegalArgumentException(fieldName + " must only contain letters and numbers");
        }
    }

}
