package com.vdt.authservice.config;

import com.vdt.authservice.constant.PredefinedPermission;
import com.vdt.authservice.constant.PredefinedRole;
import com.vdt.authservice.entity.Account;
import com.vdt.authservice.entity.Permission;
import com.vdt.authservice.entity.Role;
import com.vdt.authservice.repository.AccountRepository;
import com.vdt.authservice.repository.PermissionRepository;
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

import java.util.Set;
import java.util.stream.Collectors;

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
                                         PermissionRepository permissionRepository,
                                         PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.count() == 0 && accountRepository.count() == 0) {
                log.info("Initializing default permissions, roles and admin account...");

                // 1. Create Permissions
                Set<Permission> allPermissions = Set.of(
                        createPermission(permissionRepository, PredefinedPermission.ACCOUNT_READ, "Grants permission to view account lists"),
                        createPermission(permissionRepository, PredefinedPermission.ACCOUNT_DEACTIVATE, "Allows deactivating user accounts"),
                        createPermission(permissionRepository, PredefinedPermission.ACCOUNT_ASSIGN_ROLE, "Enables assigning or revoking roles"),
                        createPermission(permissionRepository, PredefinedPermission.ROLE_READ, "Grants access to view the list of available roles"),
                        createPermission(permissionRepository, PredefinedPermission.ROLE_WRITE, "Allows creating new roles or modifying existing ones"),
                        createPermission(permissionRepository, PredefinedPermission.ROLE_DELETE, "Marks a role as deleted"),
                        createPermission(permissionRepository, PredefinedPermission.PERMISSION_READ, "Enables viewing the complete list of system permissions"),
                        createPermission(permissionRepository, PredefinedPermission.PERMISSION_WRITE, "Allows defining and adding new functional permissions"),
                        createPermission(permissionRepository, PredefinedPermission.PERMISSION_DELETE, "Marks a permission as deleted")
                );

                // 2. Create Roles
                Role adminRole = Role.builder()
                        .name(PredefinedRole.ADMIN)
                        .description("Administrator Role")
                        .permissions(allPermissions)
                        .build();

                roleRepository.save(adminRole);

                // 3. Create Admin Account
                Account adminAccount = Account.builder()
                        .email(adminEmail)
                        .username("admin")
                        .password(passwordEncoder.encode(adminPassword))
                        .isActive(true)
                        .isEmailVerified(true)
                        .roles(Set.of(adminRole))
                        .build();
                
                accountRepository.save(adminAccount);

                log.info("Initialization completed successfully.");
            }
        };
    }

    private Permission createPermission(PermissionRepository repository, String name, String description) {
        return repository.findByName(name).orElseGet(() -> {
            Permission p = Permission.builder().name(name).description(description).build();
            return repository.save(p);
        });
    }
}
