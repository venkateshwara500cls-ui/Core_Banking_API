package com.corebanking.api.controller;

import com.corebanking.api.entity.Accounts;
import com.corebanking.api.service.BankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class BankingController {

    private final BankingService bankingService;

    public BankingController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @GetMapping
    public List<Accounts> getAllAccounts() {
        return bankingService.getAllAccounts();
    }

    @GetMapping("/{accountNo}")
    public ResponseEntity<?> getAccount(@PathVariable String accountNo) {
        try {
            return ResponseEntity.ok(bankingService.getAccountByNumber(accountNo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody Map<String, String> payload) {
        try {
            String fromAcc = payload.get("fromAccount");
            String toAcc = payload.get("toAccount");
            BigDecimal amount = new BigDecimal(payload.get("amount"));

            String result = bankingService.transferFunds(fromAcc, toAcc, amount);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", result));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "FAILED", "error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("status", "FAILED", "error", e.getMessage()));
        }
    }
    
    @GetMapping("/holders/{accountType}")
    public List<String> getAccountHoldersByType(@PathVariable String accountType) {
        return bankingService.getAccountHolderNamesByType(accountType);
    }
    
    @GetMapping("/balance/{accountNo}")
	public ResponseEntity<?> getAccountBalance(@PathVariable String accountNo) {
		try {
			return ResponseEntity.ok(bankingService.getAccountBalance(accountNo));
		} catch (RuntimeException e) {
			return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
		}
	}
}