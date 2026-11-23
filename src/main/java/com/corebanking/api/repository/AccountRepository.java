package com.corebanking.api.repository;

import com.corebanking.api.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Accounts, Long> {
	Optional<Accounts> findByAccountNo(String accountNo);

	List<Accounts> findByCustomerId(Long customerId);
}
