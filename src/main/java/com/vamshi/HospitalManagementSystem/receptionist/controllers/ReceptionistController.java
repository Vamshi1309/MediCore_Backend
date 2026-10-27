package com.vamshi.HospitalManagementSystem.receptionist.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vamshi.HospitalManagementSystem.common.ApiResponse;
import com.vamshi.HospitalManagementSystem.receptionist.dtos.ReceptionistProfileResponse;
import com.vamshi.HospitalManagementSystem.receptionist.dtos.UpdateReceptionistProfileRequest;
import com.vamshi.HospitalManagementSystem.receptionist.services.ReceptionistService;
import com.vamshi.HospitalManagementSystem.admin.dtos.UserSummaryResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/receptionist")
@RequiredArgsConstructor
public class ReceptionistController {

        private final ReceptionistService receptionistService;

        @GetMapping("/profile")
        public ResponseEntity<ApiResponse<ReceptionistProfileResponse>> getMyProfile() {

                ReceptionistProfileResponse response = receptionistService
                                .getMyProfile();

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Profile fetched successfully", response));
        }

        @PutMapping("/profile")
        public ResponseEntity<ApiResponse<ReceptionistProfileResponse>> updateMyProfile(
                        @Valid @RequestBody UpdateReceptionistProfileRequest request) {

                ReceptionistProfileResponse response = receptionistService
                                .updateMyProfile(request);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Profile updated successfully", response));
        }

        @GetMapping("/patients")
        public ResponseEntity<ApiResponse<java.util.List<UserSummaryResponse>>> getAllPatients() {

                java.util.List<UserSummaryResponse> patients = receptionistService.getAllPatients();

                return ResponseEntity.ok(
                                ApiResponse.success("Patients fetched successfully", patients));
        }
}