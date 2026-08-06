package com.vamshi.HospitalManagementSystem.prescription.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PrescriptionResponse {
    private UUID prescriptionId;
    private UUID appointmentId;

    private UUID doctorId;
    private String doctorName;

    private UUID patientId;
    private String patientName;

    private String notes;

    private List<PrescriptionItemResponse> items;

    private LocalDateTime createdAt;
}
