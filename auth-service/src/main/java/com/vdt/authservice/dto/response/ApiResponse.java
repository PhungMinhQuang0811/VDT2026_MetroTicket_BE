package com.vdt.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ApiResponse<T> {
    @Builder.Default
    private int code = 1000;

    @Builder.Default
    private String message = "Success";

    private T result;
}
