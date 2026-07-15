package com.vamshi.HospitalManagementSystem.auth.services;

import com.vamshi.HospitalManagementSystem.auth.dto.AuthResponse;
import com.vamshi.HospitalManagementSystem.auth.dto.LoginRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.RefreshTokenRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.RefreshTokenResponse;
import com.vamshi.HospitalManagementSystem.auth.dto.RegisterRequest;
import com.vamshi.HospitalManagementSystem.auth.dto.UserProfileResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(String accessToken);

    UserProfileResponse getMe();
}
