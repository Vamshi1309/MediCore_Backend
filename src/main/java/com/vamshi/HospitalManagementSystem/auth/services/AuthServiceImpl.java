package com.vamshi.HospitalManagementSystem.auth.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.vamshi.HospitalManagementSystem.auth.entities.RefreshTokenEntity;
import com.vamshi.HospitalManagementSystem.auth.repositories.RefreshTokenRepository;
import com.vamshi.HospitalManagementSystem.auth.security.JwtUtil;
import com.vamshi.HospitalManagementSystem.common.enums.Role;
import com.vamshi.HospitalManagementSystem.exceptions.BadRequestException;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceAlreadyExistsException;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceNotFoundException;
import com.vamshi.HospitalManagementSystem.otp.OtpService;
import com.vamshi.HospitalManagementSystem.patient.entities.PatientProfileEntity;
import com.vamshi.HospitalManagementSystem.patient.repositories.PatientProfileRepository;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;
import com.vamshi.HospitalManagementSystem.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;

        private final PasswordEncoder passwordEncoder;

        private final AuthenticationManager authenticationManager;

        private final PatientProfileRepository patientProfileRepository;

        private final RefreshTokenRepository refreshTokenRepository;

        private final TokenBlacklistService tokenBlacklistService;

        private final JwtUtil jwtUtil;

        private final OtpService otpService;

        @Override
        public AuthResponse patientLogin(PatientLoginRequest request) {

                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getPhoneNumber(),
                                                request.getPassword()));

                UserEntity user = userRepository.findByPhoneNumber(
                                request.getPhoneNumber())
                                .orElseThrow(() -> new ResourceNotFoundException("User not exist"));

                return generateAuthResponse(user);
        }

        @Override
        public AuthResponse staffLogin(StaffLoginRequest request) {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getStaffId(),
                                request.getPassword()));

                UserEntity user = userRepository.findByStaffIdAndRoleNot(
                                request.getStaffId(), Role.PATIENT)
                                .orElseThrow(() -> new ResourceNotFoundException("User not exist"));

                UserDetails userDetails = buildUserDetails(user);

                String accessToken = jwtUtil.generateAccessToken(userDetails);
                String refreshToken = generateAndSaveRefreshToken(
                                user, userDetails);

                AuthResponse response = new AuthResponse();
                response.setAccessToken(accessToken);
                response.setRefreshToken(refreshToken);

                return response;
        }

        @Override
        public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
                String oldToken = request.getRefreshToken();

                RefreshTokenEntity storedToken = refreshTokenRepository
                                .findByToken(oldToken)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Invalid refresh token"));

                if (storedToken.isRevoked()) {
                        throw new IllegalStateException(
                                        "Refresh token already used. " +
                                                        "Possible token theft detected. " +
                                                        "Please login again.");
                }

                if (storedToken.getExpiryDate()
                                .isBefore(LocalDateTime.now())) {
                        throw new IllegalStateException(
                                        "Refresh token expired. Please login again.");
                }

                String tokenType = jwtUtil.extractClaim(oldToken,
                                claims -> claims.get("type", String.class));

                if (!"REFRESH".equals(tokenType)) {
                        throw new IllegalArgumentException(
                                        "Invalid token type. Refresh token required.");
                }

                storedToken.setRevoked(true);
                refreshTokenRepository.save(storedToken);

                UserEntity user = storedToken.getUser();
                UserDetails userDetails = buildUserDetails(user);

                String newAccessToken = jwtUtil.generateAccessToken(userDetails);

                String newRefreshToken = generateAndSaveRefreshToken(
                                user, userDetails);

                return RefreshTokenResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(newRefreshToken)
                                .build();
        }

        // ── Helper — generate + save refresh token ───────────
        private String generateAndSaveRefreshToken(
                        UserEntity user, UserDetails userDetails) {

                String refreshToken = jwtUtil
                                .generateRefreshToken(userDetails);

                RefreshTokenEntity tokenEntity = RefreshTokenEntity
                                .builder()
                                .token(refreshToken)
                                .user(user)
                                .expiryDate(LocalDateTime.now()
                                                .plusDays(7))
                                .revoked(false)
                                .build();

                refreshTokenRepository.save(tokenEntity);

                return refreshToken;
        }

        @Override
        public void logout(String accessToken) {
                long expirationTime = jwtUtil.extractClaim(
                                accessToken,
                                claims -> claims.getExpiration().getTime());

                long remainingTime = expirationTime
                                - System.currentTimeMillis();

                if (remainingTime > 0) {
                        tokenBlacklistService.blacklistToken(
                                        accessToken, remainingTime);
                }

        }

        @Override
        public UserProfileResponse getMe() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                String identifier = authentication.getName();

                System.out.println("========== GET ME ==========");
                System.out.println("Authentication: " + authentication);
                System.out.println("Principal: " + authentication.getPrincipal());
                System.out.println("Name: " + authentication.getName());
                System.out.println("Identifier: [" + identifier + "]");
                System.out.println("============================");

                UserEntity user = userRepository.findByPhoneNumber(identifier)
                                .or(() -> userRepository.findByStaffIdAndRoleNot(identifier, Role.PATIENT))
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                return UserProfileResponse.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .phoneNumber(user.getPhoneNumber())
                                .role(user.getRole().name())
                                .build();
        }

        @Override
        public void changePassword(ChangePasswordRequest request) {
                String identifier = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();

                UserEntity user = userRepository.findByPhoneNumber(identifier)
                                .or(() -> userRepository.findByStaffIdAndRoleNot(identifier, Role.PATIENT))
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        throw new IllegalArgumentException("Current password is incorrect");
                }

                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                user.setMustChangePassword(false);
                userRepository.save(user);
        }

        @Override
        public OtpResponse sendRegistrationOtp(SendOtpRequest request) {
                if (userRepository.existsByphoneNumber(request.getPhoneNumber())) {
                        throw new ResourceAlreadyExistsException("User with this phone number already exists");
                }

                otpService.sendOtp(request.getPhoneNumber());

                return OtpResponse.builder()
                                .message("OTP sent successfully.")
                                .build();
        }

        @Override
        @Transactional
        public OtpResponse verifyRegistrationOtp(VerifyRegistrationOtpRequest request) {
                // Verify OTP
                if (!otpService.verifyOtp(
                                request.getPhoneNumber(),
                                request.getOtp())) {

                        throw new BadRequestException("Invalid or expired OTP.");
                }

                // Double-check phone number
                if (userRepository.existsByphoneNumber(request.getPhoneNumber())) {
                        throw new ResourceAlreadyExistsException(
                                        "Phone number already registered.");
                }

                UserEntity user = UserEntity.builder()
                                .name(request.getName())
                                .phoneNumber(request.getPhoneNumber())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.PATIENT)
                                .build();

                userRepository.save(user);

                PatientProfileEntity patientProfile = PatientProfileEntity.builder()
                                .user(user)
                                .build();

                patientProfileRepository.save(patientProfile);

                return OtpResponse.builder()
                                .message("Registration completed successfully.")
                                .build();

        }

        @Override
        public OtpResponse sendLoginOtp(SendOtpRequest request) {
                UserEntity user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No account found with this phone number."));

                if (user.getRole() != Role.PATIENT) {
                        throw new BadRequestException(
                                        "OTP login is available only for patients.");
                }

                if (!Boolean.TRUE.equals(user.getIsActive())) {
                        throw new BadRequestException(
                                        "Your account is inactive. Please contact support.");
                }

                otpService.sendOtp(request.getPhoneNumber());

                return OtpResponse.builder()
                                .message("OTP sent successfully.")
                                .build();
        }

        @Override
        @Transactional
        public AuthResponse verifyLoginOtp(VerifyLoginOtpRequest request) {
                // Verify OTP
                if (!otpService.verifyOtp(request.getPhoneNumber(), request.getOtp())) {
                        throw new BadRequestException("Invalid or expired OTP.");
                }

                // Find user
                UserEntity user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No account found with this phone number."));

                if (user.getRole() != Role.PATIENT) {
                        throw new BadRequestException(
                                        "OTP login is available only for patients.");
                }

                if (!Boolean.TRUE.equals(user.getIsActive())) {
                        throw new BadRequestException(
                                        "Your account is inactive. Please contact support.");
                }

                // Generate tokens
                return generateAuthResponse(user);
        }

        private UserDetails buildUserDetails(UserEntity user) {
                String principal = user.getRole() == Role.PATIENT
                                ? user.getPhoneNumber()
                                : user.getStaffId();

                return new User(
                                principal,
                                user.getPassword(),
                                List.of(new SimpleGrantedAuthority(
                                                "ROLE_" + user.getRole().name())));
        }

        private AuthResponse generateAuthResponse(UserEntity user) {

                UserDetails userDetails = buildUserDetails(user);

                String accessToken = jwtUtil.generateAccessToken(userDetails);
                String refreshToken = jwtUtil.generateRefreshToken(userDetails);

                RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                                .token(refreshToken)
                                .user(user)
                                .expiryDate(LocalDateTime.now().plusDays(7))
                                .revoked(false)
                                .build();

                refreshTokenRepository.save(refreshTokenEntity);

                AuthResponse response = new AuthResponse();
                response.setAccessToken(accessToken);
                response.setRefreshToken(refreshToken);

                return response;
        }
}
