package com.coding.exercise.bankapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.coding.exercise.bankapp.model.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, String> {

    public Optional<Customer> findByCustomerNumber(Long customerNumber);

    @Query("SELECT c.firstName, c.lastName, c.customerNumber FROM Customer c")
    List<Object[]> findAllCustomerNames();
}
