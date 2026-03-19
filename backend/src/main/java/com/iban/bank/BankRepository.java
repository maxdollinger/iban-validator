package com.iban.bank;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link BankCode} entities.
 *
 * <p>
 * The inherited {@link #existsById(Object)} method is the primary operation
 * used by
 * {@link DeBankCodeValidator} to check whether a bank code is registered.
 *
 * <p>
 * <strong>Note:</strong> No datasource is configured yet. This interface is
 * defined now
 * so that the production validator ({@link DeBankCodeValidator}) can be written
 * against the
 * real contract. Wire a datasource and swap {@link DeBankCodeValidatorStub} for
 * {@link DeBankCodeValidator} in {@link BankCodeValidators} when persistence is
 * added.
 */
public interface BankRepository extends JpaRepository<Bank, String> {
}
