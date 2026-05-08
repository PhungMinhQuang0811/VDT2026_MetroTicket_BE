package com.vdt.authservice.security.auth;

import com.nimbusds.jwt.SignedJWT;
import com.vdt.authservice.exception.AppException;
import com.vdt.authservice.exception.ErrorCode;
import com.vdt.authservice.security.entity.CustomUserDetails;
import com.vdt.authservice.security.service.TokenManagementService;
import com.vdt.authservice.security.service.UserPermissionService;
import com.vdt.authservice.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.experimental.NonFinal;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    JwtUtil jwtUtil;
    TokenManagementService tokenManagementService;
    UserPermissionService userPermissionService;

    @NonFinal
    @Value("${app.security.jwt.access-token-cookie-name}")
    String accessTokenCookieName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = null;
        if (request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(cookie -> accessTokenCookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (token != null && !tokenManagementService.isAccessTokenInvalidated(token)) {
            try {
                SignedJWT signedJWT = jwtUtil.verifyAccessToken(token);
                String userId = signedJWT.getJWTClaimsSet().getSubject();
                String email = signedJWT.getJWTClaimsSet().getStringClaim("email");
                String username = signedJWT.getJWTClaimsSet().getStringClaim("username");

                var authorities = userPermissionService.getUserPermissions(userId);

                CustomUserDetails userDetails = CustomUserDetails.builder()
                        .id(userId)
                        .email(email)
                        .username(username)
                        .authorities(authorities)
                        .build();

                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
        }

        filterChain.doFilter(request, response);
    }
}
