package com.vamshi.HospitalManagementSystem.auth.dto;

import java.util.UUID;

import com.vamshi.HospitalManagementSystem.common.enums.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String accessToken;

    private String refreshToken;

    private UUID id;

    private String phoneNumber;

    private String staffId;

    private Role role;

    private String name;

    private Boolean mustChangePassword;
}
