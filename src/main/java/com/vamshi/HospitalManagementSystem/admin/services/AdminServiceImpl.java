package com.vamshi.HospitalManagementSystem.admin.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vamshi.HospitalManagementSystem.admin.dtos.AdminStatsResponse;
import com.vamshi.HospitalManagementSystem.admin.dtos.CreateStaffRequest;
import com.vamshi.HospitalManagementSystem.admin.dtos.StaffSummaryResponse;
import com.vamshi.HospitalManagementSystem.admin.dtos.UpdateUserStatusRequest;
import com.vamshi.HospitalManagementSystem.appointment.repositories.AppointmentRepository;
import com.vamshi.HospitalManagementSystem.common.enums.Role;
import com.vamshi.HospitalManagementSystem.common.utils.AuthUtil;
import com.vamshi.HospitalManagementSystem.doctor.entities.DoctorProfileEntity;
import com.vamshi.HospitalManagementSystem.doctor.repositories.DoctorProfileRepository;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceAlreadyExistsException;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceNotFoundException;
import com.vamshi.HospitalManagementSystem.pharmacist.entities.PharmacistProfileEntity;
import com.vamshi.HospitalManagementSystem.pharmacist.repositories.PharmacistProfileRepository;
import com.vamshi.HospitalManagementSystem.radiologist.entities.RadiologistEntity;
import com.vamshi.HospitalManagementSystem.radiologist.repositories.RadiologistProfileRepository;
import com.vamshi.HospitalManagementSystem.receptionist.entities.ReceptionistProfile;
import com.vamshi.HospitalManagementSystem.receptionist.repositories.ReceptionistProfileRepository;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;
import com.vamshi.HospitalManagementSystem.user.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final List<Role> STAFF_ROLES = List.of(
            Role.DOCTOR, Role.RECEPTIONIST,
            Role.PHARMACIST, Role.RADIOLOGIST);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StaffIdGeneratorService staffIdGenerator;
    private final AppointmentRepository appointmentRepository;
    private final AuthUtil authUtil;
    private final DoctorProfileRepository doctorProfileRepository;
    private final ReceptionistProfileRepository receptionistProfileRepository;
    private final PharmacistProfileRepository pharmacistProfileRepository;
    private final RadiologistProfileRepository radiologistProfileRepository;

    // ── Create Staff ─────────────────────────────────────
    @Override
    @Transactional
    public StaffSummaryResponse createStaff(CreateStaffRequest request) {

        if (!STAFF_ROLES.contains(request.getRole())) {
            throw new IllegalArgumentException(
                "Role must be DOCTOR, RECEPTIONIST, PHARMACIST or RADIOLOGIST");
        }

        // blank → null (unique column allows many nulls, not many "")
        String phone = blankToNull(request.getPhoneNumber());
        String email = blankToNull(request.getEmail());

        if (phone != null && userRepository.existsByPhoneNumber(phone)) {
            throw new ResourceAlreadyExistsException("Phone number already registered");
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .phoneNumber(phone)
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .staffId(staffIdGenerator.generateStaffId(request.getRole()))
                .isActive(true)     // set explicitly, builder ignores field default
                .build();

        UserEntity saved = userRepository.save(user);

        switch (saved.getRole()) {
            case DOCTOR -> doctorProfileRepository.save(
                    DoctorProfileEntity.builder().user(saved).build());
            case RECEPTIONIST -> receptionistProfileRepository.save(
                    ReceptionistProfile.builder().user(saved).build());
            case PHARMACIST -> pharmacistProfileRepository.save(
                    PharmacistProfileEntity.builder().user(saved).build());
            case RADIOLOGIST -> radiologistProfileRepository.save(
                    RadiologistEntity.builder().user(saved).build());
            default -> { }
        }

        return mapToStaffResponse(saved);
    }

    // ── List / Detail ────────────────────────────────────
    @Override
    public List<StaffSummaryResponse> getAllStaff() {
        return userRepository.findByRoleIn(STAFF_ROLES)
                .stream()
                .map(this::mapToStaffResponse)
                .toList();
    }

    @Override
    public StaffSummaryResponse getStaffById(UUID id) {
        return mapToStaffResponse(findStaffOrThrow(id));
    }

    // ── Activate / Deactivate ────────────────────────────
    @Override
    @Transactional
    public StaffSummaryResponse updateStaffStatus(
            UUID id, UpdateUserStatusRequest request) {

        UserEntity staff = findStaffOrThrow(id);

        if (staff.getId().equals(authUtil.getLoggedInUser().getId())) {
            throw new IllegalArgumentException("You cannot change your own status");
        }

        staff.setIsActive(request.getIsActive());
        return mapToStaffResponse(userRepository.save(staff));
    }

    // ── Stats ────────────────────────────────────────────
    @Override
    public AdminStatsResponse getStats() {

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return AdminStatsResponse.builder()
                .totalPatients(userRepository.countByRole(Role.PATIENT))
                .totalDoctors(userRepository.countByRole(Role.DOCTOR))
                .totalOtherStaff(
                        userRepository.countByRole(Role.RECEPTIONIST)
                        + userRepository.countByRole(Role.PHARMACIST)
                        + userRepository.countByRole(Role.RADIOLOGIST))
                .todaysAppointments(
                        appointmentRepository.countByScheduledAtBetween(start, end))
                .build();
    }

    // ── Helpers ──────────────────────────────────────────
    private UserEntity findStaffOrThrow(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        // patients/admin are not staff → treat as not found
        if (!STAFF_ROLES.contains(user.getRole())) {
            throw new ResourceNotFoundException("Staff not found");
        }
        return user;
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private StaffSummaryResponse mapToStaffResponse(UserEntity user) {
        return StaffSummaryResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .staffId(user.getStaffId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}