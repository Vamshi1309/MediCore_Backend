package com.vamshi.HospitalManagementSystem.auth.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String name;
    private String phoneNumber;
    private String role;
}
