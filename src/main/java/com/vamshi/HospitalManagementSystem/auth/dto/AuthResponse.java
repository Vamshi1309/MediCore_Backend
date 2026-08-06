package com.vamshi.HospitalManagementSystem.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String accessToken;

    private String refreshToken;
}
