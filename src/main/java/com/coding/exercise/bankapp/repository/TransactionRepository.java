package com.coding.exercise.bankapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.coding.exercise.bankapp.model.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    public Optional<List<Transaction>> findByAccountNumber(Long accountNumber);
    
}
