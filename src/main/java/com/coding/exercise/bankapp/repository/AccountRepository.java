package com.coding.exercise.bankapp.repository;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coding.exercise.bankapp.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

	Optional<Account> findByAccountNumber(Long accountNumber);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
	Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") Long accountNumber);
}
