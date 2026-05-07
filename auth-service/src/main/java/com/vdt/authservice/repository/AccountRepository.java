package com.vdt.authservice.repository;

import com.vdt.authservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<Account> findByEmail(String email);
    
    // TODO: Viết query tay (JPQL) tối ưu để load Account cùng Roles/Permissions thay cho EntityGraph
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<Account> findById(String id);
}
