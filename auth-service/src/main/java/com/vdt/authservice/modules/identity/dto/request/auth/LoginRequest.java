package com.vdt.authservice.modules.identity.dto.request.auth;

import com.vdt.authservice.modules.identity.validation.RequiredField;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @RequiredField(fieldName = "Identifier")
    String identifier;

    @RequiredField(fieldName = "Password")
    String password;
}
