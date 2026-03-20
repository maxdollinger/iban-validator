package com.iban.bank;

import org.springframework.stereotype.Service;

import com.iban.iban.Iban.Iban;

import java.util.Optional;

@Service
public class BankService {

    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public Optional<Bank> getBankByIban(Iban iban) {
        return bankRepository.findByCountryCodeAndBankCode(iban.countryCode(), iban.extractBankAccount());
    }

    public Bank upsertBank(String countryCode, String bankCode, String name, String accountAlgo) {
        Bank bank = bankRepository.findByCountryCodeAndBankCode(countryCode, bankCode)
                .orElse(new Bank(bankCode, countryCode, name, accountAlgo));

        bank.setName(name);
        bank.setAccountAlgo(accountAlgo);

        return bankRepository.save(bank);
    }

}
