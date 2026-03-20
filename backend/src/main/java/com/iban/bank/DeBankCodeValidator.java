package com.iban.bank;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@ConditionalOnBean(DataSource.class)
public class DeBankCodeValidator implements BankCodeValidator {

    private final BankRepository repository;

    public DeBankCodeValidator(BankRepository repository) {
        this.repository = repository;
    }

    public boolean isKnown(String bankCode) {
        return repository.existsById(bankCode);
    }
}
