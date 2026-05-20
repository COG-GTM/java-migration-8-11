package com.coding.exercise.bankapp.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.BankInfo;
import com.coding.exercise.bankapp.model.Contact;
import com.coding.exercise.bankapp.model.Customer;
import com.coding.exercise.bankapp.model.CustomerAccountXRef;
import com.coding.exercise.bankapp.model.Transaction;
import com.coding.exercise.bankapp.repository.AccountRepository;
import com.coding.exercise.bankapp.repository.CustomerAccountXRefRepository;
import com.coding.exercise.bankapp.repository.CustomerRepository;
import com.coding.exercise.bankapp.repository.TransactionRepository;

@Component
public class DashboardDataInitializer implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerAccountXRefRepository custAccXRefRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            return;
        }

        Address address = Address.builder()
                .address1("Av. Paseo de la Reforma 510")
                .address2("Col. Juarez")
                .city("Mexico City")
                .state("CDMX")
                .zip("06600")
                .country("Mexico")
                .build();

        Contact contact = Contact.builder()
                .emailId("carlos.rodriguez@bbva.mx")
                .homePhone("+52-55-1234-5678")
                .workPhone("+52-55-8765-4321")
                .build();

        Customer customer = Customer.builder()
                .firstName("Carlos")
                .lastName("Rodriguez")
                .middleName("A")
                .customerNumber(1001L)
                .status("Active")
                .customerAddress(address)
                .contactDetails(contact)
                .createDateTime(new Date())
                .build();
        customerRepository.save(customer);

        Address branchAddr1 = Address.builder()
                .address1("Av. Paseo de la Reforma 510")
                .address2("Piso 20")
                .city("Mexico City")
                .state("CDMX")
                .zip("06600")
                .country("Mexico")
                .build();

        BankInfo bankInfo1 = BankInfo.builder()
                .branchCode(1234)
                .branchName("BBVA Torre Reforma")
                .routingNumber(123456789)
                .branchAddress(branchAddr1)
                .build();

        Account checking = Account.builder()
                .accountNumber(5001L)
                .accountType("CHECKING")
                .accountStatus("Active")
                .accountBalance(15750.50)
                .bankInformation(bankInfo1)
                .createDateTime(new Date())
                .build();
        accountRepository.save(checking);

        Address branchAddr2 = Address.builder()
                .address1("Av. Paseo de la Reforma 510")
                .address2("Piso 20")
                .city("Mexico City")
                .state("CDMX")
                .zip("06600")
                .country("Mexico")
                .build();

        BankInfo bankInfo2 = BankInfo.builder()
                .branchCode(1234)
                .branchName("BBVA Torre Reforma")
                .routingNumber(123456789)
                .branchAddress(branchAddr2)
                .build();

        Account savings = Account.builder()
                .accountNumber(5002L)
                .accountType("SAVINGS")
                .accountStatus("Active")
                .accountBalance(42300.00)
                .bankInformation(bankInfo2)
                .createDateTime(new Date())
                .build();
        accountRepository.save(savings);

        custAccXRefRepository.save(CustomerAccountXRef.builder()
                .accountNumber(5001L).customerNumber(1001L).build());
        custAccXRefRepository.save(CustomerAccountXRef.builder()
                .accountNumber(5002L).customerNumber(1001L).build());

        transactionRepository.save(Transaction.builder()
                .accountNumber(5001L).txDateTime(new Date()).txType("DEBIT").txAmount(2500.00).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5002L).txDateTime(new Date()).txType("CREDIT").txAmount(2500.00).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5002L).txDateTime(new Date()).txType("DEBIT").txAmount(500.00).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5001L).txDateTime(new Date()).txType("CREDIT").txAmount(500.00).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5001L).txDateTime(new Date()).txType("DEBIT").txAmount(1200.00).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5002L).txDateTime(new Date()).txType("CREDIT").txAmount(1200.00).build());
    }
}
