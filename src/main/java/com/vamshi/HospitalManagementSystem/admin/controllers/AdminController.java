package com.vamshi.HospitalManagementSystem.admin.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vamshi.HospitalManagementSystem.admin.dtos.AdminStatsResponse;
import com.vamshi.HospitalManagementSystem.admin.dtos.CreateStaffRequest;
import com.vamshi.HospitalManagementSystem.admin.dtos.StaffSummaryResponse;
import com.vamshi.HospitalManagementSystem.admin.dtos.UpdateUserStatusRequest;
import com.vamshi.HospitalManagementSystem.admin.services.AdminService;
import com.vamshi.HospitalManagementSystem.common.ApiResponse;
import com.vamshi.HospitalManagementSystem.patient.services.PatientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.vamshi.HospitalManagementSystem.patient.dtos.PatientSummaryResponse;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

        private final AdminService adminService;
        private final PatientService patientService;

        @PostMapping("/staff")
        public ResponseEntity<ApiResponse<StaffSummaryResponse>> createStaff(
                        @RequestBody @Valid CreateStaffRequest request) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Staff created successfully",
                                                adminService.createStaff(request)));
        }

        @GetMapping("/staff")
        public ResponseEntity<ApiResponse<List<StaffSummaryResponse>>> getAllStaff() {
                return ResponseEntity.ok(ApiResponse.success("Staff fetched successfully",
                                adminService.getAllStaff()));
        }

        @GetMapping("/patients")
        public ResponseEntity<ApiResponse<List<PatientSummaryResponse>>> getAllUsers() {
                return ResponseEntity.ok(ApiResponse.success("Patients fetched successfully",
                                patientService.getAllPatients()));
        }

        @GetMapping("/staff/{id}")
        public ResponseEntity<ApiResponse<StaffSummaryResponse>> getStaffById(
                        @PathVariable UUID id) {
                return ResponseEntity.ok(ApiResponse.success("Staff fetched successfully",
                                adminService.getStaffById(id)));
        }

        @PatchMapping("/staff/{id}/status")
        public ResponseEntity<ApiResponse<StaffSummaryResponse>> updateStatus(
                        @PathVariable UUID id,
                        @RequestBody @Valid UpdateUserStatusRequest request) {
                return ResponseEntity.ok(ApiResponse.success("Status updated successfully",
                                adminService.updateStaffStatus(id, request)));
        }

        @GetMapping("/stats")
        public ResponseEntity<ApiResponse<AdminStatsResponse>> getStats() {
                return ResponseEntity.ok(ApiResponse.success("Stats fetched successfully",
                                adminService.getStats()));
        }
}
