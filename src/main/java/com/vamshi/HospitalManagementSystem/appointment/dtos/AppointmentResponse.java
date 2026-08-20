package com.vamshi.HospitalManagementSystem.appointment.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.vamshi.HospitalManagementSystem.common.enums.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentResponse {

    private UUID appointmentId;
    private UUID patientId;
    private String patientName;
    private UUID doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private UUID createdById;
    private String createdByName;
    private LocalDateTime scheduledAt;
    private AppointmentStatus status;
    private String notes;
    private LocalDateTime createdAt;
}
