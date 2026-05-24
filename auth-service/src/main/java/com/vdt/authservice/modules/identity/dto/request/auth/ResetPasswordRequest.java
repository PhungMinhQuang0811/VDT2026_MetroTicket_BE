package com.vdt.authservice.modules.identity.dto.request.auth;

import com.vdt.authservice.modules.identity.validation.PasswordConstraint;
import com.vdt.authservice.modules.identity.validation.RequiredField;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequest {
    @RequiredField(fieldName = "Token")
    String token;

    @RequiredField(fieldName = "New Password")
    @PasswordConstraint
    String newPassword;
}
