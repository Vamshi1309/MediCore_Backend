package com.vamshi.HospitalManagementSystem.doctor.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vamshi.HospitalManagementSystem.common.ApiResponse;
import com.vamshi.HospitalManagementSystem.doctor.dtos.DoctorProfileResponse;
import com.vamshi.HospitalManagementSystem.doctor.dtos.UpdateDoctorProfileRequest;
import com.vamshi.HospitalManagementSystem.doctor.services.DoctorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<DoctorProfileResponse>> getMyProfile() {

        DoctorProfileResponse response = doctorService.getMyProfile();

        return ResponseEntity.ok(ApiResponse.success("Fetched profile successfully", response));

    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<DoctorProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateDoctorProfileRequest request) {
        DoctorProfileResponse response = doctorService.updateMyProfile(request);

        return ResponseEntity.ok(ApiResponse.success("Profile Updated Successfully", response));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<DoctorProfileResponse>>> getAllDoctors() {

        List<DoctorProfileResponse> doctors = doctorService.getAllDoctors();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Doctors fetched successfully",
                        doctors));
    }

}
