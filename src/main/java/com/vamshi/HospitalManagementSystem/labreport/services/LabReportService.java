package com.vamshi.HospitalManagementSystem.labreport.services;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.vamshi.HospitalManagementSystem.labreport.dtos.CreateLabReportRequest;
import com.vamshi.HospitalManagementSystem.labreport.dtos.LabReportResponse;

public interface LabReportService {

    LabReportResponse createLabReport(CreateLabReportRequest request, MultipartFile file);

    LabReportResponse getLabReportById(UUID id);

    List<LabReportResponse> getLabReportByPatientId(UUID patientId);

    List<LabReportResponse> getLabReportByAppointmentId(UUID appointmentId);

    String getDownloadUrl(UUID reportId);

    List<LabReportResponse> getMyLabReports();
}
