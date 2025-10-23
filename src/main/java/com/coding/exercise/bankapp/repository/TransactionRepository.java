package com.coding.exercise.bankapp.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.coding.exercise.bankapp.model.Transaction;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, String> {

    public Optional<List<Transaction>> findByAccountNumber(Long accountNumber);
    
    public Optional<List<Transaction>> findByAccountNumberAndTxDateTimeAfter(Long accountNumber, Date cutoffDate);
    
}
