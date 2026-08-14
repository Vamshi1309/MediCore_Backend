package com.vamshi.HospitalManagementSystem.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vamshi.HospitalManagementSystem.auth.dto.AuthResponse;
import com.vamshi.HospitalManagementSystem.auth.dto.ChangePasswordRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.OtpResponse;
import com.vamshi.HospitalManagementSystem.auth.dto.PatientLoginRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.RefreshTokenRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.RefreshTokenResponse;
import com.vamshi.HospitalManagementSystem.auth.dto.SendOtpRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.StaffLoginRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.UserProfileResponse;
import com.vamshi.HospitalManagementSystem.auth.dto.VerifyLoginOtpRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.VerifyRegistrationOtpRequest;
import com.vamshi.HospitalManagementSystem.auth.services.AuthService;
import com.vamshi.HospitalManagementSystem.common.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

        final AuthService authService;

        @PostMapping("/login")
        public ResponseEntity<ApiResponse<AuthResponse>> login(
                        @RequestBody @Valid PatientLoginRequest request) {

                AuthResponse response = authService.patientLogin(request);

                return ResponseEntity.status(HttpStatus.OK)
                                .body(ApiResponse.success(
                                                "Login successful",
                                                response));
        }

        @PostMapping("/staff/login")
        public ResponseEntity<ApiResponse<AuthResponse>> staffLogin(
                        @RequestBody @Valid StaffLoginRequest request) {

                AuthResponse response = authService.staffLogin(request);

                return ResponseEntity.status(HttpStatus.OK)
                                .body(ApiResponse.success(
                                                "Login successful",
                                                response));
        }

        @PostMapping("/refresh")
        public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
                        @RequestBody @Valid RefreshTokenRequest request) {

                RefreshTokenResponse response = authService
                                .refreshToken(request);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Token refreshed successfully", response));
        }

        @PostMapping("/logout")
        public ResponseEntity<ApiResponse<Void>> logout(
                        HttpServletRequest request) {

                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        authService.logout(token);
                }

                return ResponseEntity.ok(
                                ApiResponse.success("Logged out successfully"));
        }

        @GetMapping("/me")
        public ResponseEntity<ApiResponse<UserProfileResponse>> getMe() {

                UserProfileResponse response = authService.getMe();

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "User fetched successfully", response));
        }

        @PostMapping("/change-password")
        public ResponseEntity<ApiResponse<Void>> changePassword(
                        @RequestBody @Valid ChangePasswordRequest request) {

                authService.changePassword(request);

                return ResponseEntity.ok(
                                ApiResponse.success("Password changed successfully"));
        }

        @PostMapping("/register/send-otp")
        public ResponseEntity<ApiResponse<OtpResponse>> sendRegistrationOtp(
                        @Valid @RequestBody SendOtpRequest request) {

                OtpResponse response = authService.sendRegistrationOtp(request);

                return ResponseEntity.ok(
                                ApiResponse.success("OTP sent successfully.", response));
        }

        @PostMapping("/register/verify-otp")
        public ResponseEntity<ApiResponse<OtpResponse>> verifyRegistrationOtp(
                        @Valid @RequestBody VerifyRegistrationOtpRequest request) {

                OtpResponse response = authService.verifyRegistrationOtp(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success(
                                                "Registration completed successfully.",
                                                response));
        }

        @PostMapping("/login/send-otp")
        public ResponseEntity<ApiResponse<OtpResponse>> sendLoginOtp(
                        @Valid @RequestBody SendOtpRequest request) {

                OtpResponse response = authService.sendLoginOtp(request);

                return ResponseEntity.ok(
                                ApiResponse.success("OTP sent successfully.", response));
        }

        @PostMapping("/login/verify-otp")
        public ResponseEntity<ApiResponse<AuthResponse>> verifyLoginOtp(
                        @Valid @RequestBody VerifyLoginOtpRequest request) {

                AuthResponse response = authService.verifyLoginOtp(request);

                return ResponseEntity.ok(
                                ApiResponse.success("Login successful.", response));
        }
}
