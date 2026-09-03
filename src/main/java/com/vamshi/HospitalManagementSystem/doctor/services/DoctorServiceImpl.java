package com.vamshi.HospitalManagementSystem.doctor.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vamshi.HospitalManagementSystem.doctor.dtos.DoctorProfileResponse;
import com.vamshi.HospitalManagementSystem.common.utils.AuthUtil;
import com.vamshi.HospitalManagementSystem.doctor.dtos.UpdateDoctorProfileRequest;
import com.vamshi.HospitalManagementSystem.doctor.entities.DoctorProfileEntity;
import com.vamshi.HospitalManagementSystem.doctor.repositories.DoctorProfileRepository;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceNotFoundException;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorProfileRepository doctorRepository;

    // private final UserRepository userRepository;

    private final AuthUtil authUtil;

    @Override
    public DoctorProfileResponse getMyProfile() {

        UserEntity user = authUtil.getLoggedInUser();

        DoctorProfileEntity doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with that Id"));

        return mapToResponse(doctor);
    }

    @Override
    public DoctorProfileResponse updateMyProfile(UpdateDoctorProfileRequest request) {
        UserEntity user = authUtil.getLoggedInUser();

        DoctorProfileEntity doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with that Id"));

        if (request.getSpecialization() != null)
            doctor.setSpecialization(request.getSpecialization());

        if (request.getQualification() != null)
            doctor.setQualification(request.getQualification());

        if (request.getExperienceInYears() != null)
            doctor.setExperienceInYears(request.getExperienceInYears());

        if (request.getAvailabilityJson() != null)
            doctor.setAvailabilityJson(request.getAvailabilityJson());

        DoctorProfileEntity saved = doctorRepository.save(doctor);

        return mapToResponse(saved);
    }

    private DoctorProfileResponse mapToResponse(DoctorProfileEntity doctor) {
        return DoctorProfileResponse.builder()
                .userId(doctor.getUser().getId())
                .name(doctor.getUser().getName())
                .email(doctor.getUser().getEmail())
                .phoneNumber(doctor.getUser().getPhoneNumber())
                .specialization(doctor.getSpecialization())
                .experienceInYears(doctor.getExperienceInYears())
                .qualification(doctor.getQualification())
                .availabilityJson(doctor.getAvailabilityJson())
                .build();
    }

    @Override
    @Transactional
    public List<DoctorProfileResponse> getAllDoctors() {

        return doctorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}
