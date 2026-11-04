package com.vamshi.HospitalManagementSystem.admin.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsResponse {
    private long totalPatients;
    private long totalDoctors;
    private long totalOtherStaff;     // receptionist + pharmacist + radiologist
    private long todaysAppointments;
}
