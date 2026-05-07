package com.vdt.authservice.security.entity;

import com.vdt.authservice.entity.Account;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomUserDetails {
    String id;
    String email;
    String username;
    Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Account account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.username = account.getUsername();
        this.authorities = account.getRoles().stream()
                .flatMap(role -> {
                    Stream<String> roleStream = Stream.of("ROLE_" + role.getName());
                    Stream<String> permissionStream = role.getPermissions().stream()
                            .map(com.vdt.authservice.entity.Permission::getName);
                    return Stream.concat(roleStream, permissionStream);
                })
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
