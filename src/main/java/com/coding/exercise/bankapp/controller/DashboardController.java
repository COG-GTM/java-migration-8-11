package com.coding.exercise.bankapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.NotificationItem;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferRequest;
import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Customer;
import com.coding.exercise.bankapp.model.CustomerAccountXRef;
import com.coding.exercise.bankapp.model.Transaction;
import com.coding.exercise.bankapp.repository.AccountRepository;
import com.coding.exercise.bankapp.repository.CustomerAccountXRefRepository;
import com.coding.exercise.bankapp.repository.CustomerRepository;
import com.coding.exercise.bankapp.repository.TransactionRepository;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import com.coding.exercise.bankapp.service.helper.BankingServiceHelper;

@Controller
@RequestMapping("dashboard")
public class DashboardController {

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

    @Autowired
    private BankingServiceImpl bankingService;

    @GetMapping
    public String dashboard(Model model) {
        Customer customer = getFirstCustomer();

        if (customer == null) {
            model.addAttribute("customerName", "Usuario");
            model.addAttribute("customerInitials", "U");
            model.addAttribute("totalBalance", 0.0);
            model.addAttribute("accounts", Collections.emptyList());
            model.addAttribute("transactions", Collections.emptyList());
            model.addAttribute("notifications", Collections.emptyList());
            model.addAttribute("unreadNotificationCount", 0);
            return "dashboard";
        }

        String fullName = buildFullName(customer);
        String initials = buildInitials(customer);

        model.addAttribute("customerName", fullName);
        model.addAttribute("customerInitials", initials);

        List<AccountInformation> accounts = getCustomerAccounts(customer.getCustomerNumber());
        model.addAttribute("accounts", accounts);

        double totalBalance = accounts.stream()
                .filter(a -> a.getAccountBalance() != null)
                .mapToDouble(AccountInformation::getAccountBalance)
                .sum();
        model.addAttribute("totalBalance", totalBalance);

        List<TransactionDetails> allTransactions = getAllTransactionsForAccounts(accounts);
        allTransactions.sort(Comparator.comparing(TransactionDetails::getTxDateTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        int limit = Math.min(allTransactions.size(), 10);
        model.addAttribute("transactions", allTransactions.subList(0, limit));

        List<NotificationItem> notifications = generateNotifications(accounts, allTransactions);
        model.addAttribute("notifications", notifications);

        long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
        model.addAttribute("unreadNotificationCount", unreadCount);

        return "dashboard";
    }

    @PostMapping("/transfer")
    @ResponseBody
    public ResponseEntity<Map<String, String>> transfer(@RequestBody TransferRequest request) {
        Map<String, String> response = new HashMap<>();

        Customer customer = getFirstCustomer();
        if (customer == null) {
            response.put("message", "No customer found.");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Account> fromOpt = accountRepository.findByAccountNumber(request.getFromAccountNumber());
        if (!fromOpt.isPresent()) {
            response.put("message", "Source account not found.");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Account> toOpt = accountRepository.findByAccountNumber(request.getToAccountNumber());
        if (!toOpt.isPresent()) {
            response.put("message", "Destination account not found.");
            return ResponseEntity.badRequest().body(response);
        }

        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            response.put("message", "Source and destination accounts must be different.");
            return ResponseEntity.badRequest().body(response);
        }

        Account fromAccount = fromOpt.get();
        if (fromAccount.getAccountBalance() < request.getAmount()) {
            response.put("message", "Insufficient funds.");
            return ResponseEntity.badRequest().body(response);
        }

        com.coding.exercise.bankapp.domain.TransferDetails transferDetails =
                new com.coding.exercise.bankapp.domain.TransferDetails();
        transferDetails.setFromAccountNumber(request.getFromAccountNumber());
        transferDetails.setToAccountNumber(request.getToAccountNumber());
        transferDetails.setTransferAmount(request.getAmount());

        bankingService.transferDetails(transferDetails, customer.getCustomerNumber());

        response.put("message", "Transfer completed successfully!");
        return ResponseEntity.ok(response);
    }

    private Customer getFirstCustomer() {
        Iterable<Customer> customers = customerRepository.findAll();
        for (Customer c : customers) {
            return c;
        }
        return null;
    }

    private String buildFullName(Customer customer) {
        StringBuilder sb = new StringBuilder();
        if (customer.getFirstName() != null) sb.append(customer.getFirstName());
        if (customer.getLastName() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(customer.getLastName());
        }
        return sb.length() > 0 ? sb.toString() : "Usuario";
    }

    private String buildInitials(Customer customer) {
        StringBuilder sb = new StringBuilder();
        if (customer.getFirstName() != null && !customer.getFirstName().isEmpty()) {
            sb.append(customer.getFirstName().charAt(0));
        }
        if (customer.getLastName() != null && !customer.getLastName().isEmpty()) {
            sb.append(customer.getLastName().charAt(0));
        }
        return sb.length() > 0 ? sb.toString().toUpperCase() : "U";
    }

    private List<AccountInformation> getCustomerAccounts(Long customerNumber) {
        List<AccountInformation> accounts = new ArrayList<>();
        List<CustomerAccountXRef> xrefs = custAccXRefRepository.findByCustomerNumber(customerNumber);
        for (CustomerAccountXRef xref : xrefs) {
            Optional<Account> accountOpt = accountRepository.findByAccountNumber(xref.getAccountNumber());
            accountOpt.ifPresent(account -> accounts.add(bankingServiceHelper.convertToAccountDomain(account)));
        }
        return accounts;
    }

    private List<TransactionDetails> getAllTransactionsForAccounts(List<AccountInformation> accounts) {
        List<TransactionDetails> allTx = new ArrayList<>();
        for (AccountInformation account : accounts) {
            Optional<List<Transaction>> txOpt = transactionRepository.findByAccountNumber(account.getAccountNumber());
            txOpt.ifPresent(transactions -> {
                for (Transaction tx : transactions) {
                    allTx.add(bankingServiceHelper.convertToTransactionDomain(tx));
                }
            });
        }
        return allTx;
    }

    private List<NotificationItem> generateNotifications(List<AccountInformation> accounts,
                                                          List<TransactionDetails> transactions) {
        List<NotificationItem> notifications = new ArrayList<>();

        for (AccountInformation account : accounts) {
            if (account.getAccountBalance() != null && account.getAccountBalance() < 1000) {
                notifications.add(NotificationItem.builder()
                        .message("Low balance alert: Account ****"
                                + getLastFour(account.getAccountNumber())
                                + " has $" + String.format("%.2f", account.getAccountBalance()))
                        .type("warning")
                        .timeAgo("Just now")
                        .read(false)
                        .build());
            }
        }

        for (TransactionDetails tx : transactions) {
            if (tx.getTxAmount() != null && tx.getTxAmount() >= 5000) {
                notifications.add(NotificationItem.builder()
                        .message("Large transaction: $" + String.format("%.2f", tx.getTxAmount())
                                + " " + tx.getTxType() + " on account ****"
                                + getLastFour(tx.getAccountNumber()))
                        .type("info")
                        .timeAgo("Recently")
                        .read(false)
                        .build());
            }
        }

        if (notifications.isEmpty()) {
            notifications.add(NotificationItem.builder()
                    .message("Welcome to your BBVA Dashboard!")
                    .type("success")
                    .timeAgo("Just now")
                    .read(true)
                    .build());
        }

        return notifications;
    }

    private String getLastFour(Long number) {
        String numStr = String.valueOf(number);
        return numStr.length() >= 4 ? numStr.substring(numStr.length() - 4) : numStr;
    }
}
