package com.vdt.authservice.security.service;

import com.vdt.authservice.entity.Account;
import com.vdt.authservice.exception.AppException;
import com.vdt.authservice.exception.ErrorCode;
import com.vdt.authservice.repository.AccountRepository;
import com.vdt.authservice.security.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Account account = accountRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        return new CustomUserDetails(account);
    }
}
