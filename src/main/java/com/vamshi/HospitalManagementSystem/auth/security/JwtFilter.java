package com.vamshi.HospitalManagementSystem.auth.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vamshi.HospitalManagementSystem.auth.services.TokenBlacklistService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;
        private final UserDetailsServiceImpl userService;
        private final TokenBlacklistService tokenBlacklistService;

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                final String authHeader = request.getHeader("Authorization");

                String token = null;
                String phoneNumber = null;

                if (authHeader != null
                                && authHeader.startsWith("Bearer ")) {

                        token = authHeader.substring(7);

                        if (tokenBlacklistService.isBlackListed(token)) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");

                                response.getWriter().write("""
                                                {
                                                    "message": "Access token revoked"
                                                }
                                                """);

                                return;
                        }

                        try {
                                phoneNumber = jwtUtil.extractPhoneNumber(token);
                                System.out.println("JWT PHONE NUMBER = [" + phoneNumber + "]");

                        } catch (ExpiredJwtException e) {

                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");

                                response.getWriter().write("""
                                                {
                                                    "message": "Access token expired"
                                                }
                                                """);

                                return;
                        } catch (JwtException e) {

                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");

                                response.getWriter().write("""
                                                {
                                                    "message": "Invalid access token"
                                                }
                                                """);

                                return;
                        }
                }

                if (phoneNumber != null
                                && SecurityContextHolder
                                                .getContext()
                                                .getAuthentication() == null) {

                        UserDetails userDetails = userService.loadUserByUsername(
                                        phoneNumber);

                        if (jwtUtil.validateToken(
                                        token,
                                        userDetails)) {

                                System.out.println("USER = " + userDetails.getUsername());
                                System.out.println("AUTHORITIES = " + userDetails.getAuthorities());

                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                                userDetails,
                                                null,
                                                userDetails.getAuthorities());

                                auth.setDetails(
                                                new WebAuthenticationDetailsSource()
                                                                .buildDetails(request));

                                SecurityContextHolder
                                                .getContext()
                                                .setAuthentication(auth);
                        }
                }

                filterChain.doFilter(request, response);
        }

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {

                String path = request.getServletPath();

                return path.equals("/api/auth/login")
                                || path.equals("/api/auth/staff/login")
                                || path.equals("/api/auth/refresh")
                                || path.equals("/api/auth/register/send-otp")
                                || path.equals("/api/auth/register/verify-otp")
                                || path.equals("/api/auth/login/send-otp")
                                || path.equals("/api/auth/login/verify-otp");
        }
}