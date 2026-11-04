package com.vamshi.HospitalManagementSystem.patient.dtos;

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
public class PatientSummaryResponse {
    private UUID userId;
    private String name;
    private String phoneNumber;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
