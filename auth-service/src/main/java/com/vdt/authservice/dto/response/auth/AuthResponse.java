package com.vdt.authservice.dto.response.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {
    String id;
    String username;
    Set<String> roles;
    Set<String> permissions;
}
