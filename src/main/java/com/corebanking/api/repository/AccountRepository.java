package com.corebanking.api.repository;

import com.corebanking.api.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
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
            SELECT c.full_name AS fullName,
                   a.account_no AS accountNo,
                   a.account_type AS accountType,
                   a.balance AS balance,
                   a.status AS status
            FROM accounts a
            INNER JOIN customers c ON a.customer_id = c.customer_id
            WHERE a.account_no = :accountNo
            """, nativeQuery = true)
    Optional<AccountBalanceResponse> findBalanceByAccountNo(@Param("accountNo") String accountNo);

    @Query(value = """
            SELECT c.full_name AS fullName,
                   a.account_no AS accountNo,
                   a.account_type AS accountType,
                   a.balance AS balance,
                   b.branch_name AS branchName,
                   b.ifsc_code AS ifscCode,
                   b.city AS city
            FROM accounts a
            INNER JOIN customers c ON a.customer_id = c.customer_id
            INNER JOIN branches b ON a.branch_id = b.branch_id
            WHERE UPPER(b.city) = UPPER(:city)
            """, nativeQuery = true)
    List<AccountDetailProjection> findAccountsByBranchCity(@Param("city") String city);

    @Query(value = """
            SELECT c.full_name AS fullName,
                   a.account_no AS accountNo,
                   a.account_type AS accountType,
                   a.balance AS balance,
                   b.branch_name AS branchName,
                   b.ifsc_code AS ifscCode,
                   b.city AS city
            FROM accounts a
            INNER JOIN customers c ON a.customer_id = c.customer_id
            INNER JOIN branches b ON a.branch_id = b.branch_id
            WHERE a.balance >= :minBalance AND a.status = 'ACTIVE'
            ORDER BY a.balance DESC
            """, nativeQuery = true)
    List<AccountDetailProjection> findHighValueActiveAccounts(@Param("minBalance") BigDecimal minBalance);
}