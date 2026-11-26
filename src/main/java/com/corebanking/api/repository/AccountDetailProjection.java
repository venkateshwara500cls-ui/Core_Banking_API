package com.corebanking.api.repository;

import java.math.BigDecimal;

public interface AccountDetailProjection {
    String getFullName();
    String getAccountNo();
    String getAccountType();
    BigDecimal getBalance();
    String getBranchName();
    String getIfscCode();
    String getCity();
}