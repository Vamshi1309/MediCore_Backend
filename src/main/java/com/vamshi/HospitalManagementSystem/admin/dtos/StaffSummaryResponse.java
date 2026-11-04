package com.vamshi.HospitalManagementSystem.admin.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffSummaryResponse {
    private UUID userId;
    private String name;
    private String staffId;
    private String email;
    private String phoneNumber;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
