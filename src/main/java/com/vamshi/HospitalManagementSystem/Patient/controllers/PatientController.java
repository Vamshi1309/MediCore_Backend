package com.vamshi.HospitalManagementSystem.patient.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vamshi.HospitalManagementSystem.admin.dtos.PatientSummaryResponse;
import com.vamshi.HospitalManagementSystem.common.ApiResponse;
import com.vamshi.HospitalManagementSystem.patient.dtos.PatientProfileResponse;
import com.vamshi.HospitalManagementSystem.patient.dtos.UpdatePatientProfileRequest;
import com.vamshi.HospitalManagementSystem.patient.services.PatientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

        private final PatientService patientService;

        @GetMapping("/profile")
        public ResponseEntity<ApiResponse<PatientProfileResponse>> getMyProfile() {

                PatientProfileResponse response = patientService
                                .getMyProfile();

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Profile fetched successfully", response));
        }

        @PutMapping("/profile")
        public ResponseEntity<ApiResponse<PatientProfileResponse>> updateMyProfile(
                        @Valid @RequestBody UpdatePatientProfileRequest request) {

                PatientProfileResponse response = patientService
                                .updateMyProfile(request);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Profile updated successfully", response));
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<com.vamshi.HospitalManagementSystem.patient.dtos.PatientSummaryResponse>>> getAllPatients() {
                return ResponseEntity.ok(ApiResponse.success("Patients fetched successfully",
                                patientService.getAllPatients()));
        }
}