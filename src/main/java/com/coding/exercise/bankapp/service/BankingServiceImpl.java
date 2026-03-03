package com.coding.exercise.bankapp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.Contact;
import com.coding.exercise.bankapp.model.Customer;
import com.coding.exercise.bankapp.model.CustomerAccountXRef;
import com.coding.exercise.bankapp.model.Transaction;
import com.coding.exercise.bankapp.repository.AccountRepository;
import com.coding.exercise.bankapp.repository.CustomerAccountXRefRepository;
import com.coding.exercise.bankapp.repository.CustomerRepository;
import com.coding.exercise.bankapp.repository.TransactionRepository;
import com.coding.exercise.bankapp.service.helper.BankingServiceHelper;

/**
 * Implementation of the BankingService interface.
 * Modernized to use Java 8 features including streams, Optional patterns,
 * method references, and the java.time API.
 */
@Service
@Transactional
public class BankingServiceImpl implements BankingService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerAccountXRefRepository custAccXRefRepository;
    private final BankingServiceHelper bankingServiceHelper;

    @Autowired
    public BankingServiceImpl(CustomerRepository customerRepository,
                              AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              CustomerAccountXRefRepository custAccXRefRepository,
                              BankingServiceHelper bankingServiceHelper) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.custAccXRefRepository = custAccXRefRepository;
        this.bankingServiceHelper = bankingServiceHelper;
    }
    
   
    /**
     * Find all customers using Java 8 streams and method references.
     *
     * @return list of all customer details
     */
    public List<CustomerDetails> findAll() {

        return StreamSupport.stream(customerRepository.findAll().spliterator(), false)
                .map(bankingServiceHelper::convertToCustomerDomain)
                .collect(Collectors.toList());
    }

    /**
     * CREATE Customer using Java 8 java.time API.
     * 
     * @param customerDetails the customer details to create
     * @return response entity with creation status
     */
	public ResponseEntity<Object> addCustomer(CustomerDetails customerDetails) {
		
		Customer customer = bankingServiceHelper.convertToCustomerEntity(customerDetails);
		customer.setCreateDateTime(LocalDateTime.now());
		customerRepository.save(customer);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully.");
	}

	/**
	 * READ Customer using Java 8 Optional.map with method reference.
	 * 
	 * @param customerNumber the customer number to look up
	 * @return customer details or null if not found
	 */
	public CustomerDetails findByCustomerNumber(Long customerNumber) {
		
		return customerRepository.findByCustomerNumber(customerNumber)
				.map(bankingServiceHelper::convertToCustomerDomain)
				.orElse(null);
	}

	/**
	 * UPDATE Customer using Java 8 Optional.map and Optional.ifPresent patterns.
	 * 
	 * @param customerDetails the updated customer details
	 * @param customerNumber the customer number to update
	 * @return response entity with update status
	 */
	public ResponseEntity<Object> updateCustomer(CustomerDetails customerDetails, Long customerNumber) {
		Optional<Customer> managedCustomerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);
		Customer unmanagedCustomerEntity = bankingServiceHelper.convertToCustomerEntity(customerDetails);
		
		return managedCustomerEntityOpt.map(managedCustomerEntity -> {
			
			Optional.ofNullable(unmanagedCustomerEntity.getContactDetails()).ifPresent(newContact -> {
				Contact managedContact = managedCustomerEntity.getContactDetails();
				if(managedContact != null) {
					managedContact.setEmailId(newContact.getEmailId());
					managedContact.setHomePhone(newContact.getHomePhone());
					managedContact.setWorkPhone(newContact.getWorkPhone());
				} else {
					managedCustomerEntity.setContactDetails(newContact);
				}
			});
			
			Optional.ofNullable(unmanagedCustomerEntity.getCustomerAddress()).ifPresent(newAddress -> {
				Address managedAddress = managedCustomerEntity.getCustomerAddress();
				if(managedAddress != null) {
					managedAddress.setAddress1(newAddress.getAddress1());
					managedAddress.setAddress2(newAddress.getAddress2());
					managedAddress.setCity(newAddress.getCity());
					managedAddress.setState(newAddress.getState());
					managedAddress.setZip(newAddress.getZip());
					managedAddress.setCountry(newAddress.getCountry());
				} else {
					managedCustomerEntity.setCustomerAddress(newAddress);
				}
			});
			
			managedCustomerEntity.setUpdateDateTime(LocalDateTime.now());
			managedCustomerEntity.setStatus(unmanagedCustomerEntity.getStatus());
			managedCustomerEntity.setFirstName(unmanagedCustomerEntity.getFirstName());
			managedCustomerEntity.setMiddleName(unmanagedCustomerEntity.getMiddleName());
			managedCustomerEntity.setLastName(unmanagedCustomerEntity.getLastName());
			
			customerRepository.save(managedCustomerEntity);
			
			return ResponseEntity.status(HttpStatus.OK).body((Object) "Success: Customer updated.");
		}).orElseGet(() -> 
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number " + customerNumber + " not found.")
		);
	}

	/**
	 * DELETE Customer using Java 8 Optional.map pattern.
	 * 
	 * @param customerNumber the customer number to delete
	 * @return response entity with deletion status
	 */
	public ResponseEntity<Object> deleteCustomer(Long customerNumber) {
		
		return customerRepository.findByCustomerNumber(customerNumber)
				.map(customer -> {
					customerRepository.delete(customer);
					return ResponseEntity.status(HttpStatus.OK).body((Object) "Success: Customer deleted.");
				})
				.orElseGet(() -> 
					ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer does not exist.")
				);
		
		//TODO: Delete all customer entries from CustomerAccountXRef
	}

	/**
	 * Find Account using Java 8 Optional.map pattern.
	 * 
	 * @param accountNumber the account number to look up
	 * @return response entity with account details or not found message
	 */
	public ResponseEntity<Object> findByAccountNumber(Long accountNumber) {
		
		return accountRepository.findByAccountNumber(accountNumber)
				.map(account -> ResponseEntity.status(HttpStatus.FOUND)
						.body((Object) bankingServiceHelper.convertToAccountDomain(account)))
				.orElseGet(() -> 
					ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Number " + accountNumber + " not found.")
				);
	}

	/**
	 * Create new account using Java 8 Optional.ifPresent pattern.
	 * 
	 * @param accountInformation the account information
	 * @param customerNumber the customer number to associate
	 * @return response entity with creation status
	 */
	public ResponseEntity<Object> addNewAccount(AccountInformation accountInformation, Long customerNumber) {
		
		customerRepository.findByCustomerNumber(customerNumber).ifPresent(customer -> {
			accountRepository.save(bankingServiceHelper.convertToAccountEntity(accountInformation));
			
			// Add an entry to the CustomerAccountXRef
			custAccXRefRepository.save(CustomerAccountXRef.builder()
					.accountNumber(accountInformation.getAccountNumber())
					.customerNumber(customerNumber)
					.build());
		});

		return ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully.");
	}

	/**
	 * Transfer funds from one account to another for a specific customer.
	 * Uses Java 8 Optional patterns and java.time API for timestamps.
	 * 
	 * @param transferDetails the transfer details
	 * @param customerNumber the customer number
	 * @return response entity with transfer status
	 */
	public ResponseEntity<Object> transferDetails(TransferDetails transferDetails, Long customerNumber) {
		
		List<Account> accountEntities = new ArrayList<>();
		
		Optional<Customer> customerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);

		if(!customerEntityOpt.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number " + customerNumber + " not found.");
		}
		
		// get FROM ACCOUNT info
		Optional<Account> fromAccountEntityOpt = accountRepository.findByAccountNumber(transferDetails.getFromAccountNumber());
		if(!fromAccountEntityOpt.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("From Account Number " + transferDetails.getFromAccountNumber() + " not found.");
		}
		Account fromAccountEntity = fromAccountEntityOpt.get();
		
		// get TO ACCOUNT info
		Optional<Account> toAccountEntityOpt = accountRepository.findByAccountNumber(transferDetails.getToAccountNumber());
		if(!toAccountEntityOpt.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("To Account Number " + transferDetails.getToAccountNumber() + " not found.");
		}
		Account toAccountEntity = toAccountEntityOpt.get();
		
		// if not sufficient funds, return 400 Bad Request
		if(fromAccountEntity.getAccountBalance() < transferDetails.getTransferAmount()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds.");
		}
		
		synchronized (this) {
			// Use Java 8 java.time API for consistent timestamps
			LocalDateTime now = LocalDateTime.now();
			
			// update FROM ACCOUNT 
			fromAccountEntity.setAccountBalance(fromAccountEntity.getAccountBalance() - transferDetails.getTransferAmount());
			fromAccountEntity.setUpdateDateTime(now);
			accountEntities.add(fromAccountEntity);
			
			// update TO ACCOUNT
			toAccountEntity.setAccountBalance(toAccountEntity.getAccountBalance() + transferDetails.getTransferAmount());
			toAccountEntity.setUpdateDateTime(now);
			accountEntities.add(toAccountEntity);
			
			accountRepository.saveAll(accountEntities);
			
			// Create transaction for FROM Account
			Transaction fromTransaction = bankingServiceHelper.createTransaction(transferDetails, fromAccountEntity.getAccountNumber(), "DEBIT");
			transactionRepository.save(fromTransaction);
			
			// Create transaction for TO Account
			Transaction toTransaction = bankingServiceHelper.createTransaction(transferDetails, toAccountEntity.getAccountNumber(), "CREDIT");
			transactionRepository.save(toTransaction);
		}

		return ResponseEntity.status(HttpStatus.OK).body("Success: Amount transferred for Customer Number " + customerNumber);
	}

	/**
	 * Get all transactions for a specific account using Java 8 streams
	 * and nested Optional.map patterns with method references.
	 * 
	 * @param accountNumber the account number
	 * @return list of transaction details
	 */
	public List<TransactionDetails> findTransactionsByAccountNumber(Long accountNumber) {
		return accountRepository.findByAccountNumber(accountNumber)
				.map(account -> transactionRepository.findByAccountNumber(accountNumber)
						.map(transactions -> transactions.stream()
								.map(bankingServiceHelper::convertToTransactionDomain)
								.collect(Collectors.toList()))
						.orElseGet(ArrayList::new))
				.orElseGet(ArrayList::new);
	}

}
