package com.coding.exercise.bankapp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coding.exercise.bankapp.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    public Optional<Customer> findByCustomerNumber(Long customerNumber);
    
}
