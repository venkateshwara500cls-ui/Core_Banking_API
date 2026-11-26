package com.corebanking.api.repository;

import com.corebanking.api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
            SELECT DISTINCT t.*
            FROM transactions t
            INNER JOIN accounts a ON (t.from_account_id = a.account_id OR t.to_account_id = a.account_id)
            WHERE a.account_no = :accountNo
            ORDER BY t.created_at DESC
            """, nativeQuery = true)
    List<Transaction> findMiniStatementByAccountNo(@Param("accountNo") String accountNo);
}