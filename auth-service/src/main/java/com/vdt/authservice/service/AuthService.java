package com.vdt.authservice.service;

import com.nimbusds.jwt.SignedJWT;
import com.vdt.authservice.dto.request.auth.LoginRequest;
import com.vdt.authservice.dto.request.user.RegisterRequest;
import com.vdt.authservice.dto.request.auth.ResetPasswordRequest;
import com.vdt.authservice.dto.response.auth.AuthResponse;
import com.vdt.authservice.dto.response.user.UserResponse;
import com.vdt.authservice.entity.Account;
import com.vdt.authservice.external.notification.email.EmailService;
import com.vdt.authservice.mapper.AuthMapper;
import com.vdt.authservice.repository.AccountRepository;
import com.vdt.authservice.security.service.VerificationTokenService;
import com.vdt.authservice.security.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    AccountRepository accountRepository;
    AuthMapper authMapper;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;
    VerificationTokenService verificationTokenService;
    EmailService emailService;

    @NonFinal
    @Value("${app.security.jwt.access-token-expiration}")
    long accessTokenExpiration;

    @NonFinal
    @Value("${app.security.jwt.refresh-token-expiration}")
    long refreshTokenExpiration;

    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(account);
        String refreshToken = jwtUtil.generateRefreshToken(account);
        
        response.addHeader(HttpHeaders.SET_COOKIE, generateCookie("accessToken", token, "/", accessTokenExpiration, true).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, generateCookie("refreshToken", refreshToken, "/", refreshTokenExpiration, true).toString());

        return authMapper.toAuthResponse(account);
    }

    @Transactional
    public void activateAccount(String token) {
        String accountId = verificationTokenService.getAccountIdByActivationToken(token);
        if (accountId == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setActive(true);
        account.setEmailVerified(true);
        accountRepository.save(account);
        
        verificationTokenService.deleteActivationToken(token);
    }

    public void forgotPassword(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String token = verificationTokenService.generateResetPasswordToken(account.getId());
        emailService.sendEmail(account.getEmail(), "Reset Password", 
                "Click here to reset: http://localhost:8081/api/v1/auth/reset-password?token=" + token);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String accountId = verificationTokenService.getAccountIdByResetPasswordToken(request.getToken());
        if (accountId == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
        
        verificationTokenService.deleteResetPasswordToken(request.getToken());
    }

    public void logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, generateCookie("accessToken", "", "/", 0, true).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, generateCookie("refreshToken", "", "/", 0, true).toString());
        
        // TODO: Blacklist the token in Redis if needed (would require getting token from request)
    }

    public void refreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new RuntimeException("Refresh token is missing");
        }
        try {
            SignedJWT signedJWT = jwtUtil.verifyToken(refreshToken, true);
            String email = signedJWT.getJWTClaimsSet().getSubject();
            
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                    
            String newAccessToken = jwtUtil.generateToken(account);
            String newRefreshToken = jwtUtil.generateRefreshToken(account);
            
            response.addHeader(HttpHeaders.SET_COOKIE, generateCookie("accessToken", newAccessToken, "/", accessTokenExpiration, true).toString());
            response.addHeader(HttpHeaders.SET_COOKIE, generateCookie("refreshToken", newRefreshToken, "/", refreshTokenExpiration, true).toString());
            

        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token", e);
        }
    }
    private ResponseCookie generateCookie(String cookieName, String cookieValue, String path, long maxAgeMiliseconds, boolean isHttpOnly) {
        return ResponseCookie
                .from(cookieName, cookieValue)
//                .path(path)
                .path("/")
//                .domain(domain)
                .maxAge(maxAgeMiliseconds / 1000) // seconds ~ 1days
                .httpOnly(isHttpOnly)
//                .secure(true)
                .secure(false)
//                .sameSite("None")
                .sameSite("Lax")
                .build();
    }
}
