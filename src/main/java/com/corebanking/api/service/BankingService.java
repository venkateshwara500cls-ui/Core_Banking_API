package com.corebanking.api.service;

import com.corebanking.api.entity.Accounts;
import com.corebanking.api.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class BankingService {

    private final AccountRepository accountRepository;

    public BankingService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Accounts> getAllAccounts() {
        return accountRepository.findAll();
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

        return "Transferred " + amount + " from " + fromAccNo + " to " + toAccNo;
    }
}