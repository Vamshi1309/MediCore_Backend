package com.vamshi.HospitalManagementSystem.prescription.dtos;

import com.vamshi.HospitalManagementSystem.common.enums.MedicineFrequency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrescriptionItemRequest {

    @NotBlank(message = "medicineName is required")
    private String medicineName;

    @NotBlank(message = "dosage is required")
    private String dosage;

    @NotNull(message = "Duration is required")
    private Integer durationDays;

    @NotNull(message = "Frequency is required")
    private MedicineFrequency frequency;

    private String instructions;
}
