package com.vdt.authservice.config;

import com.vdt.authservice.constant.PredefinedRole;
import com.vdt.authservice.entity.Account;
import com.vdt.authservice.entity.Role;
import com.vdt.authservice.repository.AccountRepository;
import com.vdt.authservice.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(prefix = "app.init", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    @NonFinal
    @Value("${app.init.admin.email}")
    String adminEmail;

    @NonFinal
    @Value("${app.init.admin.password}")
    String adminPassword;

    @Bean
    ApplicationRunner applicationRunner(AccountRepository accountRepository, 
                                        RoleRepository roleRepository, 
                                        PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.count() == 0 && accountRepository.count() == 0) {
                log.info("Initializing default roles and admin account...");
                
                Role adminRole = Role.builder()
                        .name(PredefinedRole.ADMIN)
                        .description("Administrator Role")
                        .build();
                roleRepository.save(adminRole);

                Account adminAccount = Account.builder()
                        .email(adminEmail)
                        .username("admin")
                        .password(passwordEncoder.encode(adminPassword))
                        .isActive(true)
                        .isEmailVerified(true)
                        .build();
                adminAccount.getRoles().add(adminRole);
                
                accountRepository.save(adminAccount);
                
                log.info("Default roles and admin account created successfully.");
            }
        };
    }
}
