package com.corebanking.api.service;

import com.corebanking.api.entity.Accounts;
import com.corebanking.api.entity.Transaction;
import com.corebanking.api.repository.AccountBalanceResponse;
import com.corebanking.api.repository.AccountDetailProjection;
import com.corebanking.api.repository.AccountRepository;
import com.corebanking.api.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class BankingService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public BankingService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Accounts> getAllAccounts() {
        return accountRepository.findAll();
    }

    public List<AccountDetailProjection> getAccountsByCity(String city) {
        return accountRepository.findAccountsByBranchCity(city);
    }

    public List<AccountDetailProjection> getHighValueAccounts(BigDecimal minBalance) {
        return accountRepository.findHighValueActiveAccounts(minBalance);
    }

    public List<Transaction> getMiniStatement(String accountNo) {
        return transactionRepository.findMiniStatementByAccountNo(accountNo);
    }

    public List<String> getAccountHolderNamesByType(String accountType) {
        return accountRepository.findCustomerNamesByAccountType(accountType.toUpperCase());
    }

    public AccountBalanceResponse getAccountBalance(String accountNo) {
        return accountRepository.findBalanceByAccountNo(accountNo)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNo));
    }

    public Accounts getAccountByNumber(String accountNo) {
        return accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNo));
    }

    @Transactional
    public String transferFunds(String fromAccNo, String toAccNo, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        Accounts sender = getAccountByNumber(fromAccNo);
        Accounts receiver = getAccountByNumber(toAccNo);

        if (!"ACTIVE".equalsIgnoreCase(sender.getStatus())) {
            throw new IllegalStateException("Sender account is not ACTIVE: " + sender.getStatus());
        }
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance in account: " + fromAccNo);
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        // Save transaction ledger entry
        Transaction txn = new Transaction();
        txn.setFromAccountId(sender.getAccountId());
        txn.setToAccountId(receiver.getAccountId());
        txn.setAmount(amount);
        txn.setTxnType("TRANSFER");
        txn.setStatus("SUCCESS");
        txn.setRemarks("Transfer from " + fromAccNo + " to " + toAccNo);
        transactionRepository.save(txn);

        return "Transferred " + amount + " from " + fromAccNo + " to " + toAccNo;
    }
}