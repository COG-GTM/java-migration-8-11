package com.coding.exercise.bankapp.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
public class DashboardDataSeeder implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerAccountXRefRepository custAccXRefRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            return;
        }

        Address custAddr = Address.builder()
                .address1("Av. Paseo de la Reforma 510")
                .address2("Piso 12")
                .city("Ciudad de México")
                .state("CDMX")
                .zip("06600")
                .country("Mexico")
                .build();

        Contact contact = Contact.builder()
                .emailId("maria.cruz@bbva.mx")
                .homePhone("555-0100")
                .workPhone("555-0101")
                .build();

        Customer customer = Customer.builder()
                .firstName("Maria")
                .lastName("Cruz")
                .middleName("Elena")
                .customerNumber(1000L)
                .status("Active")
                .contactDetails(contact)
                .customerAddress(custAddr)
                .createDateTime(new Date())
                .updateDateTime(new Date())
                .build();
        customerRepository.save(customer);

        Account checking = Account.builder()
                .accountNumber(5001L)
                .accountType("Checking")
                .accountStatus("Active")
                .accountBalance(18750.50)
                .bankInformation(BankInfo.builder()
                        .branchCode(101)
                        .branchName("BBVA Reforma")
                        .routingNumber(123456789)
                        .branchAddress(Address.builder()
                                .address1("Reforma 510").city("CDMX").state("CDMX").zip("06600").country("MX")
                                .build())
                        .build())
                .createDateTime(new Date())
                .updateDateTime(new Date())
                .build();
        accountRepository.save(checking);

        Account savings = Account.builder()
                .accountNumber(5002L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(42300.00)
                .bankInformation(BankInfo.builder()
                        .branchCode(102)
                        .branchName("BBVA Polanco")
                        .routingNumber(123456790)
                        .branchAddress(Address.builder()
                                .address1("Polanco 200").city("CDMX").state("CDMX").zip("11510").country("MX")
                                .build())
                        .build())
                .createDateTime(new Date())
                .updateDateTime(new Date())
                .build();
        accountRepository.save(savings);

        custAccXRefRepository.save(CustomerAccountXRef.builder()
                .accountNumber(5001L).customerNumber(1000L).build());
        custAccXRefRepository.save(CustomerAccountXRef.builder()
                .accountNumber(5002L).customerNumber(1000L).build());

        Date now = new Date();
        transactionRepository.save(Transaction.builder()
                .accountNumber(5001L).txDateTime(new Date(now.getTime() - 7200000)).txType("DEBIT").txAmount(2500.00).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5002L).txDateTime(new Date(now.getTime() - 7200000)).txType("CREDIT").txAmount(2500.00).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5002L).txDateTime(new Date(now.getTime() - 3600000)).txType("DEBIT").txAmount(750.00).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5001L).txDateTime(new Date(now.getTime() - 3600000)).txType("CREDIT").txAmount(750.00).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5001L).txDateTime(new Date(now.getTime() - 86400000)).txType("CREDIT").txAmount(15000.00).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5002L).txDateTime(new Date(now.getTime() - 259200000)).txType("CREDIT").txAmount(8500.00).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5001L).txDateTime(new Date(now.getTime() - 432000000)).txType("DEBIT").txAmount(1200.00).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5002L).txDateTime(new Date(now.getTime() - 604800000)).txType("DEBIT").txAmount(3200.00).build());
    }
}
