package com.vamshi.HospitalManagementSystem.labreport.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vamshi.HospitalManagementSystem.appointment.entities.AppointmentEntity;
import com.vamshi.HospitalManagementSystem.appointment.repositories.AppointmentRepository;
import com.vamshi.HospitalManagementSystem.common.utils.AuthUtil;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceNotFoundException;
import com.vamshi.HospitalManagementSystem.labreport.dtos.CreateLabReportRequest;
import com.vamshi.HospitalManagementSystem.labreport.dtos.LabReportResponse;
import com.vamshi.HospitalManagementSystem.labreport.entities.LabReportEntity;
import com.vamshi.HospitalManagementSystem.labreport.repositories.LabReportRepository;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabReportServiceImpl implements LabReportService {

        private final LabReportRepository labReportRepository;
        private final AppointmentRepository appointmentRepository;
        private final S3Service s3Service;
        private final AuthUtil authUtil;

        @Override
        public LabReportResponse createLabReport(CreateLabReportRequest request, MultipartFile file) {
                UserEntity radiologist = authUtil.getLoggedInUser();

                AppointmentEntity appointment = appointmentRepository.findById(request.getAppointmentId())
                                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

                String fileKey = s3Service.uploadFile(file);

                LabReportEntity labReport = LabReportEntity.builder()
                                .appointment(appointment)
                                .patient(appointment.getPatient())
                                .radiologist(radiologist)
                                .reportType(request.getReportType())
                                .reportUrl(fileKey)
                                .findings(request.getFindings())
                                .build();

                LabReportEntity savedReport = labReportRepository.save(labReport);

                return mapToResponse(savedReport);
        }

        @Override
        public LabReportResponse getLabReportById(UUID id) {
                LabReportEntity labReport = labReportRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Lab Report Not Found"));

                return mapToResponse(labReport);
        }

        @Override
        public List<LabReportResponse> getLabReportByPatientId(UUID patientId) {
                List<LabReportEntity> labReports = labReportRepository.findByPatientId(patientId);

                return labReports
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Override
        public List<LabReportResponse> getLabReportByAppointmentId(UUID appointmentId) {
                List<LabReportEntity> labReports = labReportRepository.findByAppointmentId(appointmentId);

                return labReports
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        private LabReportResponse mapToResponse(LabReportEntity labReport) {
                return LabReportResponse.builder()
                                .reportId(labReport.getId())
                                .appointmentId(labReport.getAppointment().getId())
                                .patientId(labReport.getPatient().getId())
                                .patientName(labReport.getPatient().getName())
                                .radiologistId(labReport.getRadiologist().getId())
                                .radiologistName(labReport.getRadiologist().getName())
                                .reportType(labReport.getReportType().name())
                                .reportUrl(labReport.getReportUrl())
                                .findings(labReport.getFindings())
                                .createdAt(labReport.getCreatedAt())
                                .build();
        }

        @Override
        public String getDownloadUrl(UUID reportId) {
                LabReportEntity report = labReportRepository
                                .findById(reportId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Lab report not found"));

                return s3Service.generateDownloadUrl(report.getReportUrl());
        }
}
