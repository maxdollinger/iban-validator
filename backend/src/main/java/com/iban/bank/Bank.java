package com.iban.bank;

import com.iban.iban.AccountValidationResult;
import jakarta.persistence.*;

@Entity
@Table(name = "bank", uniqueConstraints = {
        @UniqueConstraint(name = "uc_country_bank_code", columnNames = { "country_code", "bank_code" })
}, indexes = {
        @Index(name = "idx_country_code", columnList = "country_code, bank_code")
})
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bank_code", nullable = false)
    private String bankCode;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "account_algo")
    private String accountAlgo;

    // Default constructor (required by JPA)
    public Bank() {
    }

    public Bank(String bankCode, String countryCode, String name, String accountAlgo) {
        this.bankCode = bankCode;
        this.countryCode = countryCode;
        this.name = name;
        this.accountAlgo = accountAlgo;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountAlgo() {
        return accountAlgo;
    }

    public void setAccountAlgo(String accountAlgo) {
        this.accountAlgo = accountAlgo;
    }

    public AccountValidationResult accountValidation(String account) {
        if (account.isEmpty()) {
            return AccountValidationResult.INVALID;
        }

        if (this.accountAlgo == null) {
            return AccountValidationResult.NOT_IMPLEMENTED;
        }

        return switch (this.accountAlgo) {
            default -> AccountValidationResult.NOT_IMPLEMENTED;
        };
    }
}
