package com.vdt.authservice.modules.identity.mapper;

import com.vdt.authservice.modules.identity.dto.response.user.UserResponse;
import com.vdt.authservice.modules.identity.entity.Account;
import com.vdt.authservice.modules.identity.entity.Role;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserResponse toUserResponse(Account account) {
        if (account == null) return null;

        Set<String> roles = account.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .username(account.getUsername())
                .roles(roles)
                .createdAt(account.getCreatedAt())
                .build();
    }
}
