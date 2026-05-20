package com.coding.exercise.bankapp.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.coding.exercise.bankapp.model.CustomerAccountXRef;

@Repository
public interface CustomerAccountXRefRepository extends CrudRepository<CustomerAccountXRef, String> {

    Optional<CustomerAccountXRef> findByAccountNumber(Long accountNumber);
}
