package com.corebanking.api.repository;
import java.math.BigDecimal;

public interface AccountBalanceResponse {
	String getFullName();
    String getAccountNo();
    String getAccountType();
    BigDecimal getBalance();
    String getStatus();
}
