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
		return StreamSupport.stream(customerRepository.findAll().spliterator(), false)
				.map(bankingServiceHelper::convertToCustomerDomain)
				.collect(Collectors.toList());
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
		return customerRepository.findByCustomerNumber(customerNumber)
				.map(bankingServiceHelper::convertToCustomerDomain)
				.orElse(null);
	}

	/**
	 * UPDATE Customer
	 * 
	 * @param customerDetails
	 * @param customerNumber
	 * @return
	 */
	public ResponseEntity<Object> updateCustomer(CustomerDetails customerDetails, Long customerNumber) {
		return customerRepository.findByCustomerNumber(customerNumber)
				.map(managedCustomerEntity -> {
					Customer unmanagedCustomerEntity = bankingServiceHelper.convertToCustomerEntity(customerDetails);

					Optional.ofNullable(unmanagedCustomerEntity.getContactDetails()).ifPresent(unmanagedContact -> {
						Contact managedContact = managedCustomerEntity.getContactDetails();
						if (managedContact != null) {
							managedContact.setEmailId(unmanagedContact.getEmailId());
							managedContact.setHomePhone(unmanagedContact.getHomePhone());
							managedContact.setWorkPhone(unmanagedContact.getWorkPhone());
						} else {
							managedCustomerEntity.setContactDetails(unmanagedContact);
						}
					});

					Optional.ofNullable(unmanagedCustomerEntity.getCustomerAddress()).ifPresent(unmanagedAddress -> {
						Address managedAddress = managedCustomerEntity.getCustomerAddress();
						if (managedAddress != null) {
							managedAddress.setAddress1(unmanagedAddress.getAddress1());
							managedAddress.setAddress2(unmanagedAddress.getAddress2());
							managedAddress.setCity(unmanagedAddress.getCity());
							managedAddress.setState(unmanagedAddress.getState());
							managedAddress.setZip(unmanagedAddress.getZip());
							managedAddress.setCountry(unmanagedAddress.getCountry());
						} else {
							managedCustomerEntity.setCustomerAddress(unmanagedAddress);
						}
					});

					managedCustomerEntity.setStatus(unmanagedCustomerEntity.getStatus());
					managedCustomerEntity.setFirstName(unmanagedCustomerEntity.getFirstName());
					managedCustomerEntity.setMiddleName(unmanagedCustomerEntity.getMiddleName());
					managedCustomerEntity.setLastName(unmanagedCustomerEntity.getLastName());
					managedCustomerEntity.setUpdateDateTime(LocalDateTime.now());

					customerRepository.save(managedCustomerEntity);
					return ResponseEntity.status(HttpStatus.OK).<Object>body("Success: Customer updated.");
				})
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).<Object>body("Customer Number " + customerNumber + " not found."));
	}

	/**
	 * DELETE Customer
	 * 
	 * @param customerNumber
	 * @return
	 */
	public ResponseEntity<Object> deleteCustomer(Long customerNumber) {
		return customerRepository.findByCustomerNumber(customerNumber)
				.map(managedCustomerEntity -> {
					customerRepository.delete(managedCustomerEntity);
					return ResponseEntity.status(HttpStatus.OK).<Object>body("Success: Customer deleted.");
				})
				.orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).<Object>body("Customer does not exist."));

		//TODO: Delete all customer entries from CustomerAccountXRef
	}

	/**
	 * Find Account
	 * 
	 * @param accountNumber
	 * @return
	 */
	public ResponseEntity<Object> findByAccountNumber(Long accountNumber) {
		return accountRepository.findByAccountNumber(accountNumber)
				.<ResponseEntity<Object>>map(accountEntity -> ResponseEntity.status(HttpStatus.FOUND)
						.<Object>body(bankingServiceHelper.convertToAccountDomain(accountEntity)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).<Object>body("Account Number " + accountNumber + " not found."));
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
			
		}

		return ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully.");
	}

	/**
	 * Transfer funds from one account to another for a specific customer
	 * 
	 * @param transferDetails
	 * @param customerNumber
	 * @return
	 */
	public ResponseEntity<Object> transferDetails(TransferDetails transferDetails, Long customerNumber) {
		
		List<Account> accountEntities = new ArrayList<>();
		Account fromAccountEntity = null;
		Account toAccountEntity = null;
		
		Optional<Customer> customerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);

		// If customer is present
		if(customerEntityOpt.isPresent()) {
			
			// get FROM ACCOUNT info
			Optional<Account> fromAccountEntityOpt = accountRepository.findByAccountNumber(transferDetails.getFromAccountNumber());
			if(fromAccountEntityOpt.isPresent()) {
				fromAccountEntity = fromAccountEntityOpt.get();
			}
			else {
			// if from request does not exist, 404 Bad Request
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("From Account Number " + transferDetails.getFromAccountNumber() + " not found.");
			}
			
			
			// get TO ACCOUNT info
			Optional<Account> toAccountEntityOpt = accountRepository.findByAccountNumber(transferDetails.getToAccountNumber());
			if(toAccountEntityOpt.isPresent()) {
				toAccountEntity = toAccountEntityOpt.get();
			}
			else {
			// if from request does not exist, 404 Bad Request
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("To Account Number " + transferDetails.getToAccountNumber() + " not found.");
			}

			
			// if not sufficient funds, return 400 Bad Request
			if(fromAccountEntity.getAccountBalance() < transferDetails.getTransferAmount()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds.");
			}
			else {
				synchronized (this) {
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
			}
				
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
		return accountRepository.findByAccountNumber(accountNumber)
				.flatMap(accountEntity -> transactionRepository.findByAccountNumber(accountNumber))
				.map(transactionEntities -> transactionEntities.stream()
						.map(bankingServiceHelper::convertToTransactionDomain)
						.collect(Collectors.toList()))
				.orElseGet(ArrayList::new);
	}


}
