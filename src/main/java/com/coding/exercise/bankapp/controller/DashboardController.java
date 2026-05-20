package com.coding.exercise.bankapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coding.exercise.bankapp.domain.DashboardAccountInfo;
import com.coding.exercise.bankapp.domain.NotificationItem;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.model.Transaction;
import com.coding.exercise.bankapp.model.CustomerAccountXRef;
import com.coding.exercise.bankapp.repository.AccountRepository;
import com.coding.exercise.bankapp.repository.CustomerAccountXRefRepository;
import com.coding.exercise.bankapp.repository.CustomerRepository;
import com.coding.exercise.bankapp.repository.TransactionRepository;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import com.coding.exercise.bankapp.service.helper.BankingServiceHelper;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private BankingServiceImpl bankingService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerAccountXRefRepository custAccXRefRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BankingServiceHelper bankingServiceHelper;

    @GetMapping
    @Transactional(readOnly = true)
    public String dashboard(Model model) {
        List<DashboardAccountInfo> accounts = getAllAccounts();
        List<TransactionDetails> transactions = getAllTransactions(accounts);
        List<NotificationItem> notifications = buildNotifications(accounts, transactions);

        double totalBalance = accounts.stream()
                .mapToDouble(a -> a.getAccountBalance() != null ? a.getAccountBalance() : 0.0)
                .sum();

        double totalIncome = transactions.stream()
                .filter(t -> "CREDIT".equals(t.getTxType()))
                .mapToDouble(t -> t.getTxAmount() != null ? t.getTxAmount() : 0.0)
                .sum();

        double totalExpenses = transactions.stream()
                .filter(t -> "DEBIT".equals(t.getTxType()))
                .mapToDouble(t -> t.getTxAmount() != null ? t.getTxAmount() : 0.0)
                .sum();

        String customerName = getFirstCustomerName();
        String customerInitials = getInitials(customerName);
        long notificationCount = notifications.stream().filter(n -> !n.isRead()).count();

        model.addAttribute("accounts", accounts);
        model.addAttribute("transactions", transactions);
        model.addAttribute("notifications", notifications);
        model.addAttribute("totalBalance", totalBalance);
        model.addAttribute("totalAccounts", accounts.size());
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("customerName", customerName);
        model.addAttribute("customerInitials", customerInitials);
        model.addAttribute("notificationCount", notificationCount);

        return "dashboard/index";
    }

    @PostMapping("/transfer")
    @ResponseBody
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferDetails transferDetails) {
        Optional<CustomerAccountXRef> xref = custAccXRefRepository.findByAccountNumber(
                transferDetails.getFromAccountNumber());
        if (!xref.isPresent()) {
            return ResponseEntity.ok(new TransferResponse(false, "Account owner not found."));
        }

        Long customerNumber = xref.get().getCustomerNumber();
        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, customerNumber);

        boolean success = result.getStatusCode().is2xxSuccessful();
        String message = result.getBody() != null ? result.getBody().toString() : "Transfer processed.";

        return ResponseEntity.ok(new TransferResponse(success, message));
    }

    private List<DashboardAccountInfo> getAllAccounts() {
        List<DashboardAccountInfo> accounts = new ArrayList<>();
        accountRepository.findAllAccountSummaries().forEach(row ->
                accounts.add(DashboardAccountInfo.builder()
                        .accountNumber((Long) row[0])
                        .accountType((String) row[1])
                        .accountStatus((String) row[2])
                        .accountBalance(row[3] != null ? (Double) row[3] : 0.0)
                        .build()));
        return accounts;
    }

    private List<TransactionDetails> getAllTransactions(List<DashboardAccountInfo> accounts) {
        List<TransactionDetails> allTransactions = new ArrayList<>();
        for (DashboardAccountInfo acct : accounts) {
            Optional<List<Transaction>> txOpt = transactionRepository.findByAccountNumber(acct.getAccountNumber());
            txOpt.ifPresent(txList -> txList.forEach(tx ->
                    allTransactions.add(bankingServiceHelper.convertToTransactionDomain(tx))));
        }
        allTransactions.sort((a, b) -> {
            if (a.getTxDateTime() == null && b.getTxDateTime() == null) return 0;
            if (b.getTxDateTime() == null) return -1;
            if (a.getTxDateTime() == null) return 1;
            return b.getTxDateTime().compareTo(a.getTxDateTime());
        });
        return allTransactions;
    }

    private String getFirstCustomerName() {
        List<Object[]> names = customerRepository.findAllCustomerNames();
        if (!names.isEmpty()) {
            Object[] row = names.get(0);
            String first = row[0] != null ? (String) row[0] : "";
            String last = row[1] != null ? (String) row[1] : "";
            return (first + " " + last).trim();
        }
        return "Banking User";
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "U";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
        return ("" + parts[0].charAt(0)).toUpperCase();
    }

    private List<NotificationItem> buildNotifications(List<DashboardAccountInfo> accounts,
                                                       List<TransactionDetails> transactions) {
        List<NotificationItem> notifications = new ArrayList<>();

        if (accounts.isEmpty()) {
            notifications.add(NotificationItem.builder()
                    .title("Welcome to BBVA Net Cash")
                    .message("Get started by creating a customer and account via the API.")
                    .type("info")
                    .time("Just now")
                    .read(false)
                    .build());
        }

        for (DashboardAccountInfo acct : accounts) {
            if (acct.getAccountBalance() != null && acct.getAccountBalance() < 100) {
                notifications.add(NotificationItem.builder()
                        .title("Low Balance Alert")
                        .message("Account #" + acct.getAccountNumber() + " balance is below $100.00")
                        .type("warning")
                        .time("Today")
                        .read(false)
                        .build());
            }
        }

        long txCount = transactions.size();
        if (txCount > 0) {
            notifications.add(NotificationItem.builder()
                    .title("Transaction Activity")
                    .message(txCount + " transaction(s) recorded across your accounts.")
                    .type("success")
                    .time("Recent")
                    .read(true)
                    .build());
        }

        if (accounts.size() >= 2) {
            notifications.add(NotificationItem.builder()
                    .title("Quick Transfer Available")
                    .message("You can now transfer funds between your accounts.")
                    .type("info")
                    .time("Today")
                    .read(true)
                    .build());
        }

        return notifications;
    }

    static class TransferResponse {
        private boolean success;
        private String message;

        public TransferResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
