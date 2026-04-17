package com.coding.exercise.bankapp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.exception.AccountNotFoundException;
import com.coding.exercise.bankapp.exception.CustomerNotFoundException;
import com.coding.exercise.bankapp.exception.InsufficientFundsException;
import com.coding.exercise.bankapp.exception.SelfTransferException;
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

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerAccountXRefRepository custAccXRefRepository;
    private final BankingServiceHelper bankingServiceHelper;

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

    @Override
    public List<CustomerDetails> findAll() {

    	List<CustomerDetails> allCustomerDetails = new ArrayList<>();

        Iterable<Customer> customerList = customerRepository.findAll();

        customerList.forEach(customer -> {
        	allCustomerDetails.add(bankingServiceHelper.convertToCustomerDomain(customer));
        });

        return allCustomerDetails;
    }

    @Override
	public CustomerDetails addCustomer(CustomerDetails customerDetails) {

		Customer customer = bankingServiceHelper.convertToCustomerEntity(customerDetails);
		customer.setCreateDateTime(new Date());
		customerRepository.save(customer);

		return bankingServiceHelper.convertToCustomerDomain(customer);
	}

    @Override
	public CustomerDetails findByCustomerNumber(Long customerNumber) {

		Optional<Customer> customerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);

		if(customerEntityOpt.isPresent())
			return bankingServiceHelper.convertToCustomerDomain(customerEntityOpt.get());

		throw new CustomerNotFoundException(customerNumber);
	}

    @Override
	public CustomerDetails updateCustomer(CustomerDetails customerDetails, Long customerNumber) {
		Optional<Customer> managedCustomerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);
		Customer unmanagedCustomerEntity = bankingServiceHelper.convertToCustomerEntity(customerDetails);
		if(managedCustomerEntityOpt.isPresent()) {
			Customer managedCustomerEntity = managedCustomerEntityOpt.get();

			if(Optional.ofNullable(unmanagedCustomerEntity.getContactDetails()).isPresent()) {

				Contact managedContact = managedCustomerEntity.getContactDetails();
				if(managedContact != null) {
					managedContact.setEmailId(unmanagedCustomerEntity.getContactDetails().getEmailId());
					managedContact.setHomePhone(unmanagedCustomerEntity.getContactDetails().getHomePhone());
					managedContact.setWorkPhone(unmanagedCustomerEntity.getContactDetails().getWorkPhone());
				} else
					managedCustomerEntity.setContactDetails(unmanagedCustomerEntity.getContactDetails());
			}

			if(Optional.ofNullable(unmanagedCustomerEntity.getCustomerAddress()).isPresent()) {

				Address managedAddress = managedCustomerEntity.getCustomerAddress();
				if(managedAddress != null) {
					managedAddress.setAddress1(unmanagedCustomerEntity.getCustomerAddress().getAddress1());
					managedAddress.setAddress2(unmanagedCustomerEntity.getCustomerAddress().getAddress2());
					managedAddress.setCity(unmanagedCustomerEntity.getCustomerAddress().getCity());
					managedAddress.setState(unmanagedCustomerEntity.getCustomerAddress().getState());
					managedAddress.setZip(unmanagedCustomerEntity.getCustomerAddress().getZip());
					managedAddress.setCountry(unmanagedCustomerEntity.getCustomerAddress().getCountry());
				} else
					managedCustomerEntity.setCustomerAddress(unmanagedCustomerEntity.getCustomerAddress());
			}

			managedCustomerEntity.setUpdateDateTime(new Date());
			managedCustomerEntity.setStatus(unmanagedCustomerEntity.getStatus());
			managedCustomerEntity.setFirstName(unmanagedCustomerEntity.getFirstName());
			managedCustomerEntity.setMiddleName(unmanagedCustomerEntity.getMiddleName());
			managedCustomerEntity.setLastName(unmanagedCustomerEntity.getLastName());
			managedCustomerEntity.setUpdateDateTime(new Date());

			customerRepository.save(managedCustomerEntity);

			return bankingServiceHelper.convertToCustomerDomain(managedCustomerEntity);
		} else {
			throw new CustomerNotFoundException(customerNumber);
		}
	}

    @Override
	public void deleteCustomer(Long customerNumber) {

		Optional<Customer> managedCustomerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);

		if(managedCustomerEntityOpt.isPresent()) {
			Customer managedCustomerEntity = managedCustomerEntityOpt.get();
			customerRepository.delete(managedCustomerEntity);
		} else {
			throw new CustomerNotFoundException(customerNumber);
		}
	}

    @Override
	public AccountInformation findByAccountNumber(Long accountNumber) {

		Optional<Account> accountEntityOpt = accountRepository.findByAccountNumber(accountNumber);

		if(accountEntityOpt.isPresent()) {
			return bankingServiceHelper.convertToAccountDomain(accountEntityOpt.get());
		} else {
			throw new AccountNotFoundException(accountNumber);
		}
	}

    @Override
	public AccountInformation addNewAccount(AccountInformation accountInformation, Long customerNumber) {

		Optional<Customer> customerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);

		if(customerEntityOpt.isPresent()) {
			Account savedAccount = accountRepository.save(bankingServiceHelper.convertToAccountEntity(accountInformation));

			custAccXRefRepository.save(CustomerAccountXRef.builder()
					.accountNumber(accountInformation.getAccountNumber())
					.customerNumber(customerNumber)
					.build());

			return bankingServiceHelper.convertToAccountDomain(savedAccount);
		} else {
			throw new CustomerNotFoundException(customerNumber);
		}
	}

    @Override
	public String transferDetails(TransferDetails transferDetails, Long customerNumber) {

		Optional<Customer> customerEntityOpt = customerRepository.findByCustomerNumber(customerNumber);

		if(!customerEntityOpt.isPresent()) {
			throw new CustomerNotFoundException(customerNumber);
		}

		// Reject self-transfers
		Long first = Math.min(transferDetails.getFromAccountNumber(), transferDetails.getToAccountNumber());
		Long second = Math.max(transferDetails.getFromAccountNumber(), transferDetails.getToAccountNumber());

		if(first.equals(second)) {
			throw new SelfTransferException();
		}

		// Acquire pessimistic locks in deterministic order (ascending account number) to prevent deadlocks

		Account firstEntity = accountRepository
				.findByAccountNumberForUpdate(first)
				.orElseThrow(() -> new AccountNotFoundException(first));

		Account secondEntity = accountRepository
				.findByAccountNumberForUpdate(second)
				.orElseThrow(() -> new AccountNotFoundException(second));

		Account fromAccountEntity = first.equals(transferDetails.getFromAccountNumber()) ? firstEntity : secondEntity;
		Account toAccountEntity = first.equals(transferDetails.getFromAccountNumber()) ? secondEntity : firstEntity;

		if(fromAccountEntity.getAccountBalance() < transferDetails.getTransferAmount()) {
			throw new InsufficientFundsException();
		}

		// update FROM ACCOUNT
		fromAccountEntity.setAccountBalance(fromAccountEntity.getAccountBalance() - transferDetails.getTransferAmount());
		fromAccountEntity.setUpdateDateTime(new Date());

		// update TO ACCOUNT
		toAccountEntity.setAccountBalance(toAccountEntity.getAccountBalance() + transferDetails.getTransferAmount());
		toAccountEntity.setUpdateDateTime(new Date());

		List<Account> accountEntities = new ArrayList<>();
		accountEntities.add(fromAccountEntity);
		accountEntities.add(toAccountEntity);
		accountRepository.saveAll(accountEntities);

		// Create transaction for FROM Account
		Transaction fromTransaction = bankingServiceHelper.createTransaction(transferDetails, fromAccountEntity.getAccountNumber(), "DEBIT");
		transactionRepository.save(fromTransaction);

		// Create transaction for TO Account
		Transaction toTransaction = bankingServiceHelper.createTransaction(transferDetails, toAccountEntity.getAccountNumber(), "CREDIT");
		transactionRepository.save(toTransaction);

		return "Success: Amount transferred for Customer Number " + customerNumber;
	}

    @Override
	public List<TransactionDetails> findTransactionsByAccountNumber(Long accountNumber) {
		List<TransactionDetails> transactionDetails = new ArrayList<>();
		Optional<Account> accountEntityOpt = accountRepository.findByAccountNumber(accountNumber);
		if(accountEntityOpt.isPresent()) {
			Optional<List<Transaction>> transactionEntitiesOpt = transactionRepository.findByAccountNumber(accountNumber);
			if(transactionEntitiesOpt.isPresent()) {
				transactionEntitiesOpt.get().forEach(transaction -> {
					transactionDetails.add(bankingServiceHelper.convertToTransactionDomain(transaction));
				});
			}
		}

		return transactionDetails;
	}

}
