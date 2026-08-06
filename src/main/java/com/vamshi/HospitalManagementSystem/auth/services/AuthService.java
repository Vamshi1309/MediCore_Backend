package com.vamshi.HospitalManagementSystem.auth.services;

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

public interface AuthService {

    // ================= Registration =================

    OtpResponse sendRegistrationOtp(SendOtpRequest request);

    OtpResponse verifyRegistrationOtp(VerifyRegistrationOtpRequest request);

    // ================= Patient Login =================

    AuthResponse patientLogin(PatientLoginRequest request);

    OtpResponse sendLoginOtp(SendOtpRequest request);

    AuthResponse verifyLoginOtp(VerifyLoginOtpRequest request);

    // ================= Staff Login =================

    AuthResponse staffLogin(StaffLoginRequest request);

    // ================= Token =================

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(String accessToken);

    // ================= User =================

    UserProfileResponse getMe();

    void changePassword(ChangePasswordRequest request);
}
