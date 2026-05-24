package com.vdt.authservice.modules.identity.dto.request.auth;

import com.vdt.authservice.modules.identity.validation.RequiredField;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgotPasswordRequest {
    @RequiredField(fieldName = "Email")
    @Email(message = "INVALID_EMAIL")
    String email;
}
