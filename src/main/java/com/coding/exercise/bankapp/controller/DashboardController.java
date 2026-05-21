package com.coding.exercise.bankapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import com.coding.exercise.bankapp.model.CustomerAccountXRef;
import com.coding.exercise.bankapp.service.BankingServiceImpl;

@Controller
@RequestMapping("dashboard")
public class DashboardController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BankingServiceImpl bankingService;

    @GetMapping
    public String dashboard(Model model) {
        Object[] customerRow = getFirstCustomerRow();

        if (customerRow == null) {
            model.addAttribute("customerName", "Usuario");
            model.addAttribute("customerInitials", "U");
            model.addAttribute("totalBalance", 0.0);
            model.addAttribute("accounts", Collections.emptyList());
            model.addAttribute("transactions", Collections.emptyList());
            model.addAttribute("notifications", Collections.emptyList());
            model.addAttribute("unreadNotificationCount", 0);
            return "dashboard";
        }

        String firstName = (String) customerRow[0];
        String lastName = (String) customerRow[1];
        Long customerNumber = (Long) customerRow[2];

        String fullName = buildFullName(firstName, lastName);
        String initials = buildInitials(firstName, lastName);

        model.addAttribute("customerName", fullName);
        model.addAttribute("customerInitials", initials);

        List<AccountInformation> accounts = getCustomerAccountsViaJpql(customerNumber);
        model.addAttribute("accounts", accounts);

        double totalBalance = accounts.stream()
                .filter(a -> a.getAccountBalance() != null)
                .mapToDouble(AccountInformation::getAccountBalance)
                .sum();
        model.addAttribute("totalBalance", totalBalance);

        List<TransactionDetails> allTransactions = getTransactionsViaJpql(accounts);
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

        Object[] customerRow = getFirstCustomerRow();
        if (customerRow == null) {
            response.put("message", "No customer found.");
            return ResponseEntity.badRequest().body(response);
        }

        Long customerNumber = (Long) customerRow[2];

        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            response.put("message", "Source and destination accounts must be different.");
            return ResponseEntity.badRequest().body(response);
        }

        List<Object[]> fromRows = entityManager.createQuery(
                "SELECT a.accountBalance FROM Account a WHERE a.accountNumber = :num", Object[].class)
                .setParameter("num", request.getFromAccountNumber())
                .getResultList();
        if (fromRows.isEmpty()) {
            response.put("message", "Source account not found.");
            return ResponseEntity.badRequest().body(response);
        }

        List<Object[]> toRows = entityManager.createQuery(
                "SELECT a.accountBalance FROM Account a WHERE a.accountNumber = :num", Object[].class)
                .setParameter("num", request.getToAccountNumber())
                .getResultList();
        if (toRows.isEmpty()) {
            response.put("message", "Destination account not found.");
            return ResponseEntity.badRequest().body(response);
        }

        com.coding.exercise.bankapp.domain.TransferDetails transferDetails =
                new com.coding.exercise.bankapp.domain.TransferDetails();
        transferDetails.setFromAccountNumber(request.getFromAccountNumber());
        transferDetails.setToAccountNumber(request.getToAccountNumber());
        transferDetails.setTransferAmount(request.getAmount());

        bankingService.transferDetails(transferDetails, customerNumber);

        response.put("message", "Transfer completed successfully!");
        return ResponseEntity.ok(response);
    }

    private Object[] getFirstCustomerRow() {
        List<Object[]> results = entityManager.createQuery(
                "SELECT c.firstName, c.lastName, c.customerNumber FROM Customer c", Object[].class)
                .setMaxResults(1)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @SuppressWarnings("unchecked")
    private List<AccountInformation> getCustomerAccountsViaJpql(Long customerNumber) {
        List<AccountInformation> accounts = new ArrayList<>();
        List<CustomerAccountXRef> xrefs = entityManager.createQuery(
                "SELECT x FROM CustomerAccountXRef x WHERE x.customerNumber = :cn")
                .setParameter("cn", customerNumber)
                .getResultList();
        for (CustomerAccountXRef xref : xrefs) {
            List<Object[]> rows = entityManager.createQuery(
                    "SELECT a.accountNumber, a.accountType, a.accountStatus, a.accountBalance " +
                    "FROM Account a WHERE a.accountNumber = :an", Object[].class)
                    .setParameter("an", xref.getAccountNumber())
                    .getResultList();
            for (Object[] row : rows) {
                accounts.add(AccountInformation.builder()
                        .accountNumber((Long) row[0])
                        .accountType((String) row[1])
                        .accountStatus((String) row[2])
                        .accountBalance((Double) row[3])
                        .build());
            }
        }
        return accounts;
    }

    @SuppressWarnings("unchecked")
    private List<TransactionDetails> getTransactionsViaJpql(List<AccountInformation> accounts) {
        List<TransactionDetails> allTx = new ArrayList<>();
        for (AccountInformation account : accounts) {
            List<Object[]> rows = entityManager.createQuery(
                    "SELECT t.accountNumber, t.txDateTime, t.txType, t.txAmount " +
                    "FROM Transaction t WHERE t.accountNumber = :an", Object[].class)
                    .setParameter("an", account.getAccountNumber())
                    .getResultList();
            for (Object[] row : rows) {
                allTx.add(TransactionDetails.builder()
                        .accountNumber((Long) row[0])
                        .txDateTime((Date) row[1])
                        .txType((String) row[2])
                        .txAmount((Double) row[3])
                        .build());
            }
        }
        return allTx;
    }

    private String buildFullName(String firstName, String lastName) {
        StringBuilder sb = new StringBuilder();
        if (firstName != null) sb.append(firstName);
        if (lastName != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(lastName);
        }
        return sb.length() > 0 ? sb.toString() : "Usuario";
    }

    private String buildInitials(String firstName, String lastName) {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) {
            sb.append(firstName.charAt(0));
        }
        if (lastName != null && !lastName.isEmpty()) {
            sb.append(lastName.charAt(0));
        }
        return sb.length() > 0 ? sb.toString().toUpperCase() : "U";
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
