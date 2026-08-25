package com.vamshi.HospitalManagementSystem.prescription.services;

import java.util.List;
import java.util.UUID;

import com.vamshi.HospitalManagementSystem.prescription.dtos.CreatePrescriptionRequest;
import com.vamshi.HospitalManagementSystem.prescription.dtos.PrescriptionResponse;
import com.vamshi.HospitalManagementSystem.prescription.entities.PrescriptionEntity;

public interface PrescriptionService {
    PrescriptionResponse createPrescription(CreatePrescriptionRequest request);

    PrescriptionResponse getPrescriptionById(UUID id);

    List<PrescriptionResponse> getPrescriptionsByDoctor(UUID doctorId);

    List<PrescriptionResponse> getPrescriptionsByPatient(UUID patientId);

    PrescriptionEntity getPrescriptionEntityById(UUID id);

}
