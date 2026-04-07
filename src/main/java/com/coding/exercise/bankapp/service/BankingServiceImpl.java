package com.coding.exercise.bankapp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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

@Service
@Transactional
public class BankingServiceImpl implements BankingService {

	@Autowired
    private CustomerRepository customerRepository;
	@Autowired
    private AccountRepository accountRepository;
	@Autowired
    private TransactionRepository transactionRepository;
	@Autowired
    private CustomerAccountXRefRepository custAccXRefRepository;
    @Autowired
    private BankingServiceHelper bankingServiceHelper;

    public BankingServiceImpl(CustomerRepository repository) {
        this.customerRepository=repository;
    }
    
   
    public List<CustomerDetails> findAll() {
    	
    	List<CustomerDetails> allCustomerDetails = new ArrayList<>();

        Iterable<Customer> customerList = customerRepository.findAll();

        customerList.forEach(customer -> {
        	allCustomerDetails.add(bankingServiceHelper.convertToCustomerDomain(customer));
        });
        
        return allCustomerDetails;
    }

    /**
     * CREATE Customer
     * 
     * @param customerDetails
     * @return
     */
	public ResponseEntity<Object> addCustomer(CustomerDetails customerDetails) {
		
		Customer customer = bankingServiceHelper.convertToCustomerEntity(customerDetails);
		customer.setCreateDateTime(LocalDateTime.now());
		customerRepository.save(customer);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully.");
	}

	/**
	 * READ Customer
	 * 
	 * @param customerNumber
	 * @return
	 */
    
	public CustomerDetails findByCustomerNumber(Long customerNumber) {
		
		Optional<Customer> customerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);

		if(customerEntityOpt.isPresent())
			return bankingServiceHelper.convertToCustomerDomain(customerEntityOpt.get());
		
		return null;
	}

	/**
	 * UPDATE Customer
	 * 
	 * @param customerDetails
	 * @param customerNumber
	 * @return
	 */
	public ResponseEntity<Object> updateCustomer(CustomerDetails customerDetails, Long customerNumber) {
		Optional<Customer> managedCustomerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);
		Customer unmanagedCustomerEntity = bankingServiceHelper.convertToCustomerEntity(customerDetails);
		if(managedCustomerEntityOpt.isPresent()) {
			Customer managedCustomerEntity = managedCustomerEntityOpt.get();
			
			if(unmanagedCustomerEntity.getContactDetails() != null) {
				updateContact(managedCustomerEntity, unmanagedCustomerEntity.getContactDetails());
			}
			
			if(unmanagedCustomerEntity.getCustomerAddress() != null) {
				updateAddress(managedCustomerEntity, unmanagedCustomerEntity.getCustomerAddress());
			}
			
			managedCustomerEntity.setStatus(unmanagedCustomerEntity.getStatus());
			managedCustomerEntity.setFirstName(unmanagedCustomerEntity.getFirstName());
			managedCustomerEntity.setMiddleName(unmanagedCustomerEntity.getMiddleName());
			managedCustomerEntity.setLastName(unmanagedCustomerEntity.getLastName());
			managedCustomerEntity.setUpdateDateTime(LocalDateTime.now());
			
			customerRepository.save(managedCustomerEntity);
			
			return ResponseEntity.status(HttpStatus.OK).body("Success: Customer updated.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number " + customerNumber + " not found.");
		}
	}

	/**
	 * DELETE Customer
	 * 
	 * Deletes all associated CustomerAccountXRef entries and their accounts
	 * before deleting the customer to avoid orphaned data.
	 * 
	 * @param customerNumber
	 * @return
	 */
	public ResponseEntity<Object> deleteCustomer(Long customerNumber) {
		
		Optional<Customer> managedCustomerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);

		if(managedCustomerEntityOpt.isPresent()) {
			Customer managedCustomerEntity = managedCustomerEntityOpt.get();
			
			// Delete all customer entries from CustomerAccountXRef and associated accounts
			List<CustomerAccountXRef> xrefs = custAccXRefRepository.findByCustomerNumber(customerNumber);
			for (CustomerAccountXRef xref : xrefs) {
				accountRepository.findByAccountNumber(xref.getAccountNumber())
						.ifPresent(accountRepository::delete);
			}
			custAccXRefRepository.deleteAll(xrefs);
			
			customerRepository.delete(managedCustomerEntity);
			return ResponseEntity.status(HttpStatus.OK).body("Success: Customer deleted.");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer does not exist.");
		}
	}

	/**
	 * Find Account
	 * 
	 * @param accountNumber
	 * @return
	 */
	public ResponseEntity<Object> findByAccountNumber(Long accountNumber) {
		
		Optional<Account> accountEntityOpt = accountRepository.findByAccountNumber(accountNumber);

		if(accountEntityOpt.isPresent()) {
			return ResponseEntity.status(HttpStatus.FOUND).body(bankingServiceHelper.convertToAccountDomain(accountEntityOpt.get()));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Number " + accountNumber + " not found.");
		}
		
	}

	/**
	 * Create new account
	 * 
	 * @param accountInformation
	 * @param customerNumber 
	 * 
	 * @return
	 */
	public ResponseEntity<Object> addNewAccount(AccountInformation accountInformation, Long customerNumber) {
		
		Optional<Customer> customerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);

		if(customerEntityOpt.isPresent()) {
			accountRepository.save(bankingServiceHelper.convertToAccountEntity(accountInformation));
			
			// Add an entry to the CustomerAccountXRef
			custAccXRefRepository.save(CustomerAccountXRef.builder()
					.accountNumber(accountInformation.getAccountNumber())
					.customerNumber(customerNumber)
					.build());
			
			return ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found.");
		}
	}

	/**
	 * Transfer funds from one account to another for a specific customer.
	 * 
	 * All account reads, balance validation, and updates happen inside the
	 * synchronized block to prevent race conditions between concurrent transfers.
	 * Uses SERIALIZABLE isolation for database-level consistency.
	 * 
	 * Note: synchronized(this) only works within a single JVM instance.
	 * For multi-instance deployments, consider database-level pessimistic locking.
	 * 
	 * @param transferDetails
	 * @param customerNumber
	 * @return
	 */
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public ResponseEntity<Object> transferDetails(TransferDetails transferDetails, Long customerNumber) {
		
		Optional<Customer> customerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);

		// If customer is present
		if(customerEntityOpt.isPresent()) {
			
			synchronized (this) {
				// Read accounts inside synchronized block to prevent stale reads
				List<Account> accountEntities = new ArrayList<>();
				
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

				// Validate funds with fresh data
				if(fromAccountEntity.getAccountBalance() < transferDetails.getTransferAmount()) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds.");
				}
				
				// update FROM ACCOUNT 
				fromAccountEntity.setAccountBalance(fromAccountEntity.getAccountBalance() - transferDetails.getTransferAmount());
				fromAccountEntity.setUpdateDateTime(LocalDateTime.now());
				accountEntities.add(fromAccountEntity);
				
				// update TO ACCOUNT
				toAccountEntity.setAccountBalance(toAccountEntity.getAccountBalance() + transferDetails.getTransferAmount());
				toAccountEntity.setUpdateDateTime(LocalDateTime.now());
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
				
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number " + customerNumber + " not found.");
		}
		
	}

	/**
	 * Get all transactions for a specific account
	 * 
	 * @param accountNumber
	 * @return
	 */
	public List<TransactionDetails> findTransactionsByAccountNumber(Long accountNumber) {
		Optional<Account> accountEntityOpt = accountRepository.findByAccountNumber(accountNumber);
		if(accountEntityOpt.isPresent()) {
			Optional<List<Transaction>> transactionEntitiesOpt = transactionRepository.findByAccountNumber(accountNumber);
			return transactionEntitiesOpt
					.map(transactions -> transactions.stream()
							.map(bankingServiceHelper::convertToTransactionDomain)
							.collect(Collectors.toList()))
					.orElse(Collections.emptyList());
		}
		
		return Collections.emptyList();
	}

	private void updateContact(Customer managedCustomer, Contact newContact) {
		Contact managedContact = managedCustomer.getContactDetails();
		if (managedContact != null) {
			managedContact.setEmailId(newContact.getEmailId());
			managedContact.setHomePhone(newContact.getHomePhone());
			managedContact.setWorkPhone(newContact.getWorkPhone());
		} else {
			managedCustomer.setContactDetails(newContact);
		}
	}

	private void updateAddress(Customer managedCustomer, Address newAddress) {
		Address managedAddress = managedCustomer.getCustomerAddress();
		if (managedAddress != null) {
			managedAddress.setAddress1(newAddress.getAddress1());
			managedAddress.setAddress2(newAddress.getAddress2());
			managedAddress.setCity(newAddress.getCity());
			managedAddress.setState(newAddress.getState());
			managedAddress.setZip(newAddress.getZip());
			managedAddress.setCountry(newAddress.getCountry());
		} else {
			managedCustomer.setCustomerAddress(newAddress);
		}
	}

}
