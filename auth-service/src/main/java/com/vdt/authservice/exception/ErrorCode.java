package com.vdt.authservice.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    /**
     * Range 1xxx: General / Success
     */
    SUCCESS(1000, "Success", HttpStatus.OK),

    /**
     * Range 2xxx: Validation errors
     */
    // Define validation error codes here when needed (e.g., INVALID_PASSWORD, INVALID_EMAIL)

    /**
     * Range 3xxx: Business logic & Database errors
     */
    ACCOUNT_NOT_FOUND_IN_DB(3000, "The account is not exist in the system.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_USED_BY_ANY_ACCOUNT(3001, "The email address you’ve entered does not exist. Please try again.", HttpStatus.BAD_REQUEST),
    USER_EXISTED(3002, "The user was existed", HttpStatus.BAD_REQUEST),


    /**
     * Range 4xxx: Security, Authentication & System errors
     */
    UNCATEGORIZED_EXCEPTION(4000, "There was error happen during run time", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_KEY(4001, "The error key could be misspelled", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(4002, "Unauthenticated access. The access token is invalid", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(4003, "Invalid refresh token. Please try again.", HttpStatus.UNAUTHORIZED),
    INVALID_ONETIME_TOKEN(4004, "The token is invalid or this link has expired or has been used.", HttpStatus.BAD_REQUEST),
    ;

    int code;
    String message;
    HttpStatusCode httpStatusCode;
}
