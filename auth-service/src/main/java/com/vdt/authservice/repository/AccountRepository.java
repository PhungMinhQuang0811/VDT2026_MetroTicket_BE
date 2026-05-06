package com.vdt.authservice.repository;

import com.vdt.authservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByEmail(String email);
    java.util.Optional<Account> findByEmail(String email);
}
