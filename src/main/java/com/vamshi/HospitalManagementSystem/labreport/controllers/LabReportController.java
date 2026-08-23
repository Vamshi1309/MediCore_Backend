package com.vamshi.HospitalManagementSystem.labreport.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vamshi.HospitalManagementSystem.common.ApiResponse;
import com.vamshi.HospitalManagementSystem.common.enums.ReportType;
import com.vamshi.HospitalManagementSystem.labreport.dtos.CreateLabReportRequest;
import com.vamshi.HospitalManagementSystem.labreport.dtos.LabReportResponse;
import com.vamshi.HospitalManagementSystem.labreport.services.LabReportService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lab-reports")
@RequiredArgsConstructor
public class LabReportController {
    private final LabReportService labReportService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<LabReportResponse>> createLabReport(
            @RequestParam UUID appointmentId,
            @RequestParam String reportType,
            @RequestParam(required = false) String findings,
            @RequestParam("file") MultipartFile file) {

        CreateLabReportRequest request = new CreateLabReportRequest();
        request.setAppointmentId(appointmentId);
        request.setReportType(ReportType.valueOf(reportType));
        request.setFindings(findings);

        LabReportResponse response = labReportService.createLabReport(request, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lab report created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LabReportResponse>> getLabReportById(@PathVariable UUID id) {
        LabReportResponse response = labReportService.getLabReportById(id);

        return ResponseEntity.ok(ApiResponse.success("Fetched lab report successfully", response));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<LabReportResponse>>> getLabReportByPatientId(@PathVariable UUID patientId) {
        List<LabReportResponse> response = labReportService.getLabReportByPatientId(patientId);

        return ResponseEntity.ok(ApiResponse.success("Fetched lab reports successfully", response));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<List<LabReportResponse>>> getLabReportByAppointmentId(
            @PathVariable UUID appointmentId) {
        List<LabReportResponse> response = labReportService.getLabReportByAppointmentId(appointmentId);

        return ResponseEntity.ok(ApiResponse.success("Fetched lab reports successfully", response));
    }

    @GetMapping("/{id}/download-url")
    public ResponseEntity<ApiResponse<String>> getDownloadUrl(@PathVariable UUID id) {

        String downloadUrl = labReportService.getDownloadUrl(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Download URL generated", downloadUrl));
    }
}
