package com.coding.exercise.bankapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.coding.exercise.bankapp.model.Account;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {

	Optional<Account> findByAccountNumber(Long accountNumber);

	@Query("SELECT a.accountNumber, a.accountType, a.accountStatus, a.accountBalance FROM Account a")
	List<Object[]> findAllAccountSummaries();
}
