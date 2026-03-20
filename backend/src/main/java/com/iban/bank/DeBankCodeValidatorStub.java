package com.iban.bank;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(DeBankCodeValidator.class)
public class DeBankCodeValidatorStub implements BankCodeValidator {

    public boolean isKnown(String bankCode) {
        return true;
    }
}
