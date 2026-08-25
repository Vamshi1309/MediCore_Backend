package com.vamshi.HospitalManagementSystem.patient.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vamshi.HospitalManagementSystem.common.utils.AuthUtil;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceNotFoundException;
import com.vamshi.HospitalManagementSystem.patient.dtos.PatientProfileResponse;
import com.vamshi.HospitalManagementSystem.patient.dtos.UpdatePatientProfileRequest;
import com.vamshi.HospitalManagementSystem.patient.entities.PatientProfileEntity;
import com.vamshi.HospitalManagementSystem.patient.repositories.PatientProfileRepository;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;
import com.vamshi.HospitalManagementSystem.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientProfileRepository patientRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    @Override
    public PatientProfileResponse getMyProfile() {

        UserEntity user = authUtil.getLoggedInUser();

        PatientProfileEntity patient = patientRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient profile not found"));

        return mapToResponse(patient);
    }

    @Transactional
    @Override
    public PatientProfileResponse updateMyProfile(
            UpdatePatientProfileRequest request) {

        UserEntity user = authUtil.getLoggedInUser();

        PatientProfileEntity patient = patientRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient profile not found"));

        if (request.getName() != null)
            user.setName(request.getName());

        if (request.getEmail() != null)
            user.setEmail(request.getEmail());

        if (request.getPhoneNumber() != null)
            user.setPhoneNumber(request.getPhoneNumber());

        if (request.getDateOfBirth() != null)
            patient.setDateOfBirth(request.getDateOfBirth());

        if (request.getBloodGroup() != null)
            patient.setBloodGroup(request.getBloodGroup());

        if (request.getEmergencyContact() != null)
            patient.setEmergencyContact(
                    request.getEmergencyContact());

        if (request.getInsuranceInfo() != null)
            patient.setInsuranceInfo(request.getInsuranceInfo());

        userRepository.save(user);
        PatientProfileEntity saved = patientRepository
                .save(patient);

        return mapToResponse(saved);
    }

    private PatientProfileResponse mapToResponse(
            PatientProfileEntity patient) {
        return PatientProfileResponse.builder()
                .userId(patient.getUser().getId())
                .name(patient.getUser().getName())
                .email(patient.getUser().getEmail())
                .phoneNumber(patient.getUser().getPhoneNumber())
                .dateOfBirth(patient.getDateOfBirth())
                .bloodGroup(patient.getBloodGroup())
                .emergencyContact(patient.getEmergencyContact())
                .insuranceInfo(patient.getInsuranceInfo())
                .build();
    }
}