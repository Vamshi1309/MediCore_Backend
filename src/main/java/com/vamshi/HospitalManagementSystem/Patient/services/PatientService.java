package com.vamshi.HospitalManagementSystem.patient.services;

import java.util.List;

import com.vamshi.HospitalManagementSystem.patient.dtos.PatientProfileResponse;
import com.vamshi.HospitalManagementSystem.patient.dtos.UpdatePatientProfileRequest;
import com.vamshi.HospitalManagementSystem.patient.dtos.PatientSummaryResponse;

public interface PatientService {

    PatientProfileResponse getMyProfile();

    PatientProfileResponse updateMyProfile(
            UpdatePatientProfileRequest request);

    List<PatientSummaryResponse> getAllPatients();
}