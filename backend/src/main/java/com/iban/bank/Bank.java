package com.iban.bank;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Persistent entity representing a known bank code.
 *
 * <p>
 * Each row holds one country-specific bank code (e.g. a German BLZ). The table
 * is
 * intended to be populated from the official bank code lists published by
 * central banks.
 *
 * <p>
 * <strong>Note:</strong> This entity is a stub. The database schema and data
 * population
 * are not yet wired; the {@link DeBankCodeValidatorStub} is used in place of
 * the real
 * {@link DeBankCodeValidator} until a datasource is configured.
 */
@Entity
@Table(name = "bank")
public class Bank {

    /**
     * The bank code value (e.g. an 8-digit German BLZ such as {@code "37040044"}).
     * Used as the primary key.
     */
    @Id
    private String code;

    /**
     * ISO 3166-1 alpha-2 country code identifying which country this code belongs
     * to.
     */
    private String countryCode;

    /** Optional human-readable name of the bank. */
    private String bankName;

    private String bic;

    // -------------------------------------------------------------------------
    // JPA requires a no-arg constructor
    // -------------------------------------------------------------------------

    protected Bank() {
    }

    public Bank(String code, String countryCode, String bic, String bankName) {
        this.code = code;
        this.bic = bic;
        this.countryCode = countryCode;
        this.bankName = bankName;
    }

    public String getCode() {
        return code;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getBIC() {
        return bic;
    }

    public String getBankName() {
        return bankName;
    }

}
