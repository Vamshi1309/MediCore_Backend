package com.vamshi.HospitalManagementSystem.pharmacy.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vamshi.HospitalManagementSystem.common.ApiResponse;
import com.vamshi.HospitalManagementSystem.pharmacy.dtos.DispenseMedicineRequest;
import com.vamshi.HospitalManagementSystem.pharmacy.dtos.DispenseResponse;
import com.vamshi.HospitalManagementSystem.pharmacy.services.PharmacyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
public class PharmacyController {
        private final PharmacyService pharmacyService;

        @PostMapping("/dispence")
        public ResponseEntity<ApiResponse<DispenseResponse>> dispenseMedicine(
                        @Valid @RequestBody DispenseMedicineRequest request) {
                DispenseResponse response = pharmacyService
                                .dispenseMedicine(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success(
                                                "Medicine dispensed successfully",
                                                response));
        }

        @GetMapping("/history/patient/{patientId}")
        public ResponseEntity<ApiResponse<List<DispenseResponse>>> getHistoryByPatient(
                        @PathVariable UUID patientId) {

                List<DispenseResponse> response = pharmacyService
                                .getDispenseHistoryByPatient(patientId);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Dispense history fetched successfully",
                                                response));
        }

        @GetMapping("/history/pharmacist/{pharmacistId}")
        public ResponseEntity<ApiResponse<List<DispenseResponse>>> getHistoryByPharmacist(
                        @PathVariable UUID pharmacistId) {

                List<DispenseResponse> response = pharmacyService
                                .getDispenseHistoryByPharmacist(pharmacistId);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Dispense history fetched successfully",
                                                response));
        }

        @GetMapping("/history/prescription/{prescriptionId}")
        public ResponseEntity<ApiResponse<List<DispenseResponse>>> getHistoryByPrescription(
                        @PathVariable UUID prescriptionId) {

                List<DispenseResponse> response = pharmacyService
                                .getDispenseHistoryByPrescription(prescriptionId);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Dispense history fetched successfully",
                                                response));
        }
}
