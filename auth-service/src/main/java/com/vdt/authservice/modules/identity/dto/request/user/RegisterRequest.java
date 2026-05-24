package com.vdt.authservice.modules.identity.dto.request.user;

import com.vdt.authservice.modules.identity.validation.ExternalRolesConstraint;
import com.vdt.authservice.modules.identity.validation.PasswordConstraint;
import com.vdt.authservice.modules.identity.validation.RequiredField;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @RequiredField(fieldName = "Email")
    @Email(message = "INVALID_EMAIL")
    String email;

    @RequiredField(fieldName = "Username")
    String username;

    @RequiredField(fieldName = "Password")
    @PasswordConstraint
    String password;

    @RequiredField(fieldName = "Roles")
    @ExternalRolesConstraint
    Set<String> roles;
}
