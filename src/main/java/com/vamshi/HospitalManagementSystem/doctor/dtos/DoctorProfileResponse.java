package com.vamshi.HospitalManagementSystem.doctor.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorProfileResponse {
    private UUID userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String specialization;
    private String qualification;
    private Integer experienceInYears;
    private String availabilityJson;
}