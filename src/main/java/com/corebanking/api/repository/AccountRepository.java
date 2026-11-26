package com.corebanking.api.repository;

import com.corebanking.api.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Accounts, Long> {
    Optional<Accounts> findByAccountNo(String accountNo);

    List<Accounts> findByCustomerId(Long customerId);

    @Query(value = """
            SELECT DISTINCT c.full_name
            FROM customers c
            INNER JOIN accounts a ON c.customer_id = a.customer_id
            WHERE a.account_type = :accountType
            """, nativeQuery = true)
    List<String> findCustomerNamesByAccountType(@Param("accountType") String accountType);
    
    @Query(value = """
    		SELECT c.full_name, a.account_no, a.account_type, a.balance,  a.status
    		FROM accounts a
	INNER JOIN customers c ON a.customer_id = c.customer_id
    		WHERE a.account_no=:accountNo""", nativeQuery = true)
    		Optional<AccountBalanceResponse> findBalanceByAccountNo(@Param("accountNo") String accountNo);
}
