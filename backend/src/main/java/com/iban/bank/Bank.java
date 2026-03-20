package com.iban.bank;

import jakarta.persistence.*;

@Entity
@Table(name = "bank")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bank_code", nullable = false, unique = true)
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
}
