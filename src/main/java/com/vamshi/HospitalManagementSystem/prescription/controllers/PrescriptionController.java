package com.vamshi.HospitalManagementSystem.prescription.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vamshi.HospitalManagementSystem.common.ApiResponse;
import com.vamshi.HospitalManagementSystem.prescription.dtos.CreatePrescriptionRequest;
import com.vamshi.HospitalManagementSystem.prescription.dtos.PrescriptionResponse;
import com.vamshi.HospitalManagementSystem.prescription.dtos.UpdatePrescriptionRequest;
import com.vamshi.HospitalManagementSystem.prescription.entities.PrescriptionEntity;
import com.vamshi.HospitalManagementSystem.prescription.services.PdfGeneratorService;
import com.vamshi.HospitalManagementSystem.prescription.services.PrescriptionService;

import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

        private final PrescriptionService prescriptionService;

        private final PdfGeneratorService pdfGeneratorService;

        @PostMapping()
        public ResponseEntity<ApiResponse<PrescriptionResponse>> createPrescription(
                        @Valid @RequestBody CreatePrescriptionRequest request) {
                PrescriptionResponse response = prescriptionService.createPrescription(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Prescription Created Successfully ", response));
        }

        @GetMapping("/{id}")
        @Transactional
        public ResponseEntity<ApiResponse<PrescriptionResponse>> getPrescriptionById(@PathVariable UUID id) {
                PrescriptionResponse response = prescriptionService.getPrescriptionById(id);

                return ResponseEntity.ok(ApiResponse.success("Fetched Prescription by id Successfully", response));
        }

        @GetMapping("/patient/{patientId}")
        @Transactional
        public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getPrescriptionByPatientId(
                        @PathVariable UUID patientId) {
                List<PrescriptionResponse> response = prescriptionService.getPrescriptionsByPatient(patientId);

                return ResponseEntity
                                .ok(ApiResponse.success("Fetched Prescriptions by Patient id Successfully", response));
        }

        @GetMapping("/doctor/{doctorId}")
        @Transactional
        public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getPrescriptionByDoctorId(
                        @PathVariable UUID doctorId) {
                List<PrescriptionResponse> response = prescriptionService.getPrescriptionsByDoctor(doctorId);

                return ResponseEntity
                                .ok(ApiResponse.success("Fetched Prescriptions by Doctor id Successfully", response));
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<PrescriptionResponse>> updatePrescription(
                        @PathVariable UUID id,
                        @RequestBody @Valid UpdatePrescriptionRequest request) {

                PrescriptionResponse response = prescriptionService
                                .updatePrescription(id, request);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Prescription updated successfully",
                                                response));
        }

        @GetMapping("{id}/download")
        public ResponseEntity<byte[]> downloadPrescriptionPdf(
                        @PathVariable UUID id) {

                PrescriptionEntity prescription = prescriptionService
                                .getPrescriptionEntityById(id);

                byte[] pdfBytes = pdfGeneratorService
                                .generatePrescriptionPdf(prescription);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=prescription_"
                                                + id + ".pdf");

                return ResponseEntity.ok()
                                .headers(headers)
                                .contentType(MediaType.APPLICATION_PDF)
                                .body(pdfBytes);
        }

}
