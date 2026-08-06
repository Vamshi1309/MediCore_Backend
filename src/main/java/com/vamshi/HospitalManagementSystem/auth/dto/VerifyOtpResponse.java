package com.vamshi.HospitalManagementSystem.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpResponse {
    private String accessToken;
    private String refreshToken;
    private UserProfileResponse user;
}
