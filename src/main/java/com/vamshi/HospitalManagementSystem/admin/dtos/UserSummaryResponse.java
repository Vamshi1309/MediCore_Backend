package com.vamshi.HospitalManagementSystem.admin.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.vamshi.HospitalManagementSystem.common.enums.Role;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSummaryResponse {

    private UUID userId;

    private String name;

    private String email;

    private String phoneNumber;

    private String staffId;

    private Role role;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private String tempPassword;
}