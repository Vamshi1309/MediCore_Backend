package com.vamshi.HospitalManagementSystem.prescription.dtos;

import java.util.UUID;

import com.vamshi.HospitalManagementSystem.common.enums.MedicineFrequency;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PrescriptionItemResponse {
    private UUID itemId;
    private UUID medicineId;
    private String medicineName;
    private String dosage;
    private Integer durationInDays;
    private MedicineFrequency frequency;
    private String instructions;
}
