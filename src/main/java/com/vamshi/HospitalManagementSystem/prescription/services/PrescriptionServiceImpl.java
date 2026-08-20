package com.vamshi.HospitalManagementSystem.prescription.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.vamshi.HospitalManagementSystem.prescription.dtos.PrescriptionItemRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vamshi.HospitalManagementSystem.appointment.entities.AppointmentEntity;
import com.vamshi.HospitalManagementSystem.appointment.repositories.AppointmentRepository;
import com.vamshi.HospitalManagementSystem.common.enums.AppointmentStatus;
import com.vamshi.HospitalManagementSystem.common.utils.AuthUtil;
import com.vamshi.HospitalManagementSystem.doctor.entities.DoctorProfileEntity;
import com.vamshi.HospitalManagementSystem.doctor.repositories.DoctorProfileRepository;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceAlreadyExistsException;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceNotFoundException;
import com.vamshi.HospitalManagementSystem.prescription.dtos.CreatePrescriptionRequest;
import com.vamshi.HospitalManagementSystem.prescription.dtos.PrescriptionItemResponse;
import com.vamshi.HospitalManagementSystem.prescription.dtos.PrescriptionResponse;
import com.vamshi.HospitalManagementSystem.prescription.entities.PrescriptionEntity;
import com.vamshi.HospitalManagementSystem.prescription.entities.PrescriptionItemEntity;
import com.vamshi.HospitalManagementSystem.prescription.repositories.PrescriptionRepository;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;
import com.vamshi.HospitalManagementSystem.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

        private final PrescriptionRepository prescriptionRepository;

        private final AppointmentRepository appointmentRepository;

        private final DoctorProfileRepository doctorProfileRepository;

        //private final UserRepository userRepository;

        private final AuthUtil authUtil;

        @Override
        public PrescriptionResponse createPrescription(CreatePrescriptionRequest request) {
                UserEntity doctor = authUtil.getLoggedInUser();

                AppointmentEntity appointment = appointmentRepository.findById(request.getAppointmentId())
                                .orElseThrow(() -> new ResourceNotFoundException("No Appointment Found"));

                if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
                        throw new IllegalStateException("Appointment must be CONFIRMED before writing prescription");
                }

                if (prescriptionRepository.existsByAppointmentId(appointment.getId())) {
                        throw new ResourceAlreadyExistsException("Prescription Already Created");
                }

                PrescriptionEntity prescription = PrescriptionEntity.builder()
                                .doctor(doctor)
                                .patient(appointment.getPatient())
                                .appointment(appointment)
                                .notes(request.getNotes())
                                .build();

                List<PrescriptionItemEntity> items = request.getItems()
                                .stream()
                                .map((PrescriptionItemRequest itemRequest) -> PrescriptionItemEntity.builder()
                                                .medicineName(itemRequest.getMedicineName())
                                                .dosage(itemRequest.getDosage())
                                                .duration(itemRequest.getDurationDays())
                                                .instructions(itemRequest.getInstructions())
                                                .frequency(itemRequest.getFrequency())
                                                .prescription(prescription)
                                                .build())
                                .collect(Collectors.toList());

                prescription.setItems(items);

                PrescriptionEntity savedPrescription = prescriptionRepository.save(prescription);

                return mapToResponse(savedPrescription);
        }

        @Override
        public PrescriptionResponse getPrescriptionById(UUID id) {
                PrescriptionEntity prescription = prescriptionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("No Prescription with that id"));

                return mapToResponse(prescription);
        }

        @Override
        public List<PrescriptionResponse> getPrescriptionsByDoctor(UUID doctorId) {

                List<PrescriptionEntity> prescriptions = prescriptionRepository.findByDoctorId(doctorId);

                if (prescriptions.isEmpty()) {
                        throw new ResourceNotFoundException(
                                        "No prescriptions found for this doctor");
                }

                return prescriptions.stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Override
        public List<PrescriptionResponse> getPrescriptionsByPatient(UUID patientId) {
                List<PrescriptionEntity> prescriptions = prescriptionRepository.findByPatientId(patientId);

                if (prescriptions.isEmpty()) {
                        throw new ResourceNotFoundException(
                                        "No prescriptions found for this patient");
                }

                return prescriptions.stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        private PrescriptionResponse mapToResponse(PrescriptionEntity prescription) {
                List<PrescriptionItemResponse> items = prescription.getItems()
                                .stream()
                                .map(this::mapItemToResponse)
                                .toList();
                DoctorProfileEntity doctorProfile = doctorProfileRepository
                                .findByUserId(prescription.getDoctor().getId())
                                .orElse(null);

                return PrescriptionResponse.builder()
                                .prescriptionId(prescription.getId())
                                .appointmentId(prescription.getAppointment().getId())
                                .doctorId(prescription.getDoctor().getId())
                                .doctorName(prescription.getDoctor().getName())
                                .patientId(prescription.getPatient().getId())
                                .patientName(prescription.getPatient().getName())
                                .doctorSpecialization(
                                                doctorProfile != null
                                                                ? doctorProfile.getSpecialization()
                                                                : null)
                                .items(items)
                                .notes(prescription.getNotes())
                                .createdAt(prescription.getCreatedAt())
                                .build();
        }

        private PrescriptionItemResponse mapItemToResponse(
                        PrescriptionItemEntity item) {
                return PrescriptionItemResponse.builder()
                                .itemId(item.getId())
                                .medicineName(item.getMedicineName())
                                .dosage(item.getDosage())
                                .instructions(item.getInstructions())
                                .durationInDays(item.getDuration())
                                .frequency(item.getFrequency())
                                .build();
        }

        @Override
        @Transactional
        public PrescriptionEntity getPrescriptionEntityById(UUID id) {

                System.out.println("=== METHOD STARTED ===");

                PrescriptionEntity prescription = prescriptionRepository
                                .findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Prescription not found"));

                System.out.println("=== PRESCRIPTION FOUND ===");

                int size = prescription.getItems().size();

                System.out.println("=== ITEMS SIZE: " + size + " ===");

                return prescription;
        }

}
