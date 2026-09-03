package com.vamshi.HospitalManagementSystem.prescription.dtos;

import java.util.List;

import lombok.Data;

@Data
public class UpdatePrescriptionRequest {

    private List<PrescriptionItemRequest> items;
    private String notes;
}
