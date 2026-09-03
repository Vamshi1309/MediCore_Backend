package com.vamshi.HospitalManagementSystem.doctor.services;

import java.util.List;

import com.vamshi.HospitalManagementSystem.doctor.dtos.DoctorProfileResponse;
import com.vamshi.HospitalManagementSystem.doctor.dtos.UpdateDoctorProfileRequest;

public interface DoctorService {
    DoctorProfileResponse getMyProfile();

    DoctorProfileResponse updateMyProfile(
            UpdateDoctorProfileRequest request);

    List<DoctorProfileResponse> getAllDoctors();
}
