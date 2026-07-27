package com.vamshi.HospitalManagementSystem.auth.services;

import com.vamshi.HospitalManagementSystem.auth.dto.AuthResponse;
import com.vamshi.HospitalManagementSystem.auth.dto.ChangePasswordRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.PatientLoginRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.RefreshTokenRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.RefreshTokenResponse;
import com.vamshi.HospitalManagementSystem.auth.dto.RegisterRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.StaffLoginRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.UserProfileResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse patientLogin(PatientLoginRequest request);

    AuthResponse staffLogin(StaffLoginRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(String accessToken);

    UserProfileResponse getMe();

    void changePassword(ChangePasswordRequest request);
}
