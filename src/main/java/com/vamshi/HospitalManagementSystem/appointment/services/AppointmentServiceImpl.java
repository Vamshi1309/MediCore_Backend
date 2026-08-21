package com.vamshi.HospitalManagementSystem.appointment.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vamshi.HospitalManagementSystem.appointment.dtos.AppointmentResponse;
import com.vamshi.HospitalManagementSystem.appointment.dtos.CreateAppointmentRequest;
import com.vamshi.HospitalManagementSystem.appointment.dtos.UpdateAppointmentStatusRequest;
import com.vamshi.HospitalManagementSystem.appointment.entities.AppointmentEntity;
import com.vamshi.HospitalManagementSystem.appointment.repositories.AppointmentRepository;
import com.vamshi.HospitalManagementSystem.common.enums.AppointmentStatus;
import com.vamshi.HospitalManagementSystem.common.enums.Role;
import com.vamshi.HospitalManagementSystem.common.utils.AuthUtil;
import com.vamshi.HospitalManagementSystem.doctor.entities.DoctorProfileEntity;
import com.vamshi.HospitalManagementSystem.doctor.repositories.DoctorProfileRepository;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceNotFoundException;
import com.vamshi.HospitalManagementSystem.exceptions.UnauthorizedRoleException;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;
import com.vamshi.HospitalManagementSystem.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

        private final UserRepository userRepository;

        private final AppointmentRepository appointmentRepository;

        private final DoctorProfileRepository doctorProfileRepository;

        private final AuthUtil authUtil;

        @Override
        public AppointmentResponse createAppointment(CreateAppointmentRequest request) {

             
                UserEntity createdBy = authUtil.getLoggedInUser();
                Optional<UserEntity> patientOptional = userRepository.findById(request.getPatientId());
                if (patientOptional.isEmpty()) {
                        throw new ResourceNotFoundException("Patient Not Found With That ID");
                }

                UserEntity patient = patientOptional.get();
                if (patient.getRole() != Role.PATIENT) {
                        throw new UnauthorizedRoleException("Role Mismatch");
                }

                Optional<UserEntity> doctorOptional = userRepository.findById(request.getDoctorId());
                if (doctorOptional.isEmpty()) {
                        throw new ResourceNotFoundException("Doctor Not Found With That ID");
                }

                UserEntity doctor = doctorOptional.get();
                if (doctor.getRole() != Role.DOCTOR) {
                        throw new UnauthorizedRoleException("Role Mismatch");
                }

                if (createdBy.getRole() == Role.PATIENT &&
                                !createdBy.getId().equals(request.getPatientId())) {
                        throw new UnauthorizedRoleException("Patients can only book appointments for themselves");
                }

                AppointmentEntity appointment = AppointmentEntity.builder()
                                .patient(patient)
                                .doctor(doctor)
                                .scheduledAt(request.getScheduledAt())
                                .notes(request.getNotes())
                                .createdBy(createdBy)
                                .status(AppointmentStatus.PENDING)
                                .build();

                AppointmentEntity saved = appointmentRepository.save(appointment);

                return mapToResponse(saved);

        }

        @Override
        public List<AppointmentResponse> getAllAppointments() {
                return appointmentRepository
                                .findAll()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Override
        public List<AppointmentResponse> getAppointmentsByPatient(UUID patientId) {
                return appointmentRepository.findByPatientId(patientId).stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Override
        public List<AppointmentResponse> getAppointmentsByDoctor(UUID doctorId) {
                return appointmentRepository.findByDoctorId(doctorId).stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Override
        public AppointmentResponse updateAppointmentStatus(UUID appointmentId, UpdateAppointmentStatusRequest request) {

                AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Appointment not found"));
                appointment.setStatus(
                                request.getStatus());

                AppointmentEntity saved = appointmentRepository
                                .save(
                                                appointment);

                return mapToResponse(
                                saved);
        }

        @Override
        public AppointmentResponse getAppointmentById(UUID id) {
                AppointmentEntity appointment = appointmentRepository
                                .findById(id)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Appointment not found"));

                return mapToResponse(
                                appointment);
        }

        private AppointmentResponse mapToResponse(
                        AppointmentEntity appointment) {
                DoctorProfileEntity doctorProfile = doctorProfileRepository
                                .findByUserId(appointment.getDoctor().getId())
                                .orElse(null);

                return AppointmentResponse.builder()
                                .appointmentId(
                                                appointment.getId())
                                .patientId(
                                                appointment.getPatient().getId())
                                .patientName(
                                                appointment.getPatient().getName())
                                .doctorId(
                                                appointment.getDoctor().getId())
                                .doctorName(
                                                appointment.getDoctor().getName())
                                .createdById(appointment.getCreatedBy().getId())
                                .doctorSpecialization(
                                                doctorProfile != null
                                                                ? doctorProfile.getSpecialization()
                                                                : null)
                                .createdByName(appointment.getCreatedBy().getName())
                                .scheduledAt(
                                                appointment.getScheduledAt())

                                .status(
                                                appointment.getStatus())
                                .notes(
                                                appointment.getNotes())
                                .createdAt(
                                                appointment.getCreatedAt())
                                .build();
        }

}
