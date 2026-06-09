package com.coding.exercise.bankapp.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.coding.exercise.bankapp.model.Account;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

	Optional<Account> findByAccountNumber(Long accountNumber);
}
