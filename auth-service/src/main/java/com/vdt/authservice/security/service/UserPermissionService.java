package com.vdt.authservice.security.service;

import com.vdt.authservice.entity.Account;
import com.vdt.authservice.entity.Role;
import com.vdt.authservice.exception.AppException;
import com.vdt.authservice.exception.ErrorCode;
import com.vdt.authservice.repository.AccountRepository;
import com.vdt.authservice.util.RedisUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserPermissionService {
    AccountRepository accountRepository;
    RedisUtil redisUtil;

    static final String PERM_CACHE_PREFIX = "user_perms:";
    static final long PERM_CACHE_TTL = 1; // 1 day

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Collection<? extends GrantedAuthority> getUserPermissions(String accountId) {
        String cacheKey = PERM_CACHE_PREFIX + accountId;
        
        // 1. Try to get from Redis
        Set<String> permissions = redisUtil.getSet(cacheKey);
        
        if (CollectionUtils.isEmpty(permissions)) {
            // 2. Cache miss, load from DB
            permissions = loadPermissionsFromDb(accountId);
            
            // 3. Save to Redis
            if (!permissions.isEmpty()) {
                redisUtil.addSet(cacheKey, permissions, PERM_CACHE_TTL, TimeUnit.DAYS);
            }
        }

        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Set<String> loadPermissionsFromDb(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<String> perms = new ArrayList<>();
        if (!CollectionUtils.isEmpty(account.getRoles())) {
            for (Role role : account.getRoles()) {
                perms.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(p -> perms.add(p.getName()));
                }
            }
        }
        return new HashSet<>(perms);
    }

    public void invalidateCache(String accountId) {
        redisUtil.delete(PERM_CACHE_PREFIX + accountId);
    }
}
