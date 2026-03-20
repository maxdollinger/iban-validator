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

}
