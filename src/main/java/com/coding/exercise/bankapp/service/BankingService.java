package com.coding.exercise.bankapp.service;

import java.util.List;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;

public interface BankingService {

    List<CustomerDetails> findAll();

    CustomerDetails addCustomer(CustomerDetails customerDetails);

    CustomerDetails findByCustomerNumber(Long customerNumber);

    CustomerDetails updateCustomer(CustomerDetails customerDetails, Long customerNumber);

    void deleteCustomer(Long customerNumber);

    AccountInformation findByAccountNumber(Long accountNumber);

    AccountInformation addNewAccount(AccountInformation accountInformation, Long customerNumber);

    String transferDetails(TransferDetails transferDetails, Long customerNumber);

    List<TransactionDetails> findTransactionsByAccountNumber(Long accountNumber);

}
