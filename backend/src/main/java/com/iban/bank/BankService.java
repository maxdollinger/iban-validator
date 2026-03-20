package com.iban.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
                iban.extractBankAccount());
        if (bank.isEmpty()) {
            log.warn("Bank not found for country={} bankCode={}", iban.countryCode(), iban.extractBankAccount());
        }
        return bank;
    }

    public Bank upsertBank(String countryCode, String bankCode, String name, String accountAlgo) {
        boolean exists = bankRepository.findByCountryCodeAndBankCode(countryCode, bankCode).isPresent();
        Bank bank = bankRepository.findByCountryCodeAndBankCode(countryCode, bankCode)
                .orElse(new Bank(bankCode, countryCode, name, accountAlgo));

        bank.setName(name);
        bank.setAccountAlgo(accountAlgo);

        Bank saved = bankRepository.save(bank);
        log.info("{} bank: country={} bankCode={} name={}", exists ? "Updated" : "Created", countryCode, bankCode,
                name);
        return saved;
    }

}
