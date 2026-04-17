package com.coding.exercise.bankapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coding.exercise.bankapp.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    public Optional<List<Transaction>> findByAccountNumber(Long accountNumber);
    
}
