package com.vamshi.HospitalManagementSystem.admin.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vamshi.HospitalManagementSystem.admin.dtos.AssignRoleRequest;
import com.vamshi.HospitalManagementSystem.admin.dtos.CreateUserRequest;
import com.vamshi.HospitalManagementSystem.admin.dtos.UpdateUserStatusRequest;
import com.vamshi.HospitalManagementSystem.admin.dtos.UserSummaryResponse;
import com.vamshi.HospitalManagementSystem.common.enums.Role;
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

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final DoctorProfileRepository doctorProfileRepository;
        private final ReceptionistProfileRepository receptionistProfileRepository;
        private final PharmacistProfileRepository pharmacistProfileRepository;
        private final RadiologistProfileRepository radiologistProfileRepository;
        private final StaffIdGeneratorService staffIdGeneratorService;

        @Override
        @Transactional
        public UserSummaryResponse createUser(CreateUserRequest request) {
                if (request.getRole() == Role.PATIENT) {
                        throw new IllegalArgumentException("Patients must self-register, not be created by admin");
                }

                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new ResourceAlreadyExistsException("Email Already Exists");
                }

                String generatedStaffId = staffIdGeneratorService.generateStaffId(request.getRole());
                String rawTempPassword = generateTempPassword();

                UserEntity user = UserEntity.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(rawTempPassword))
                                .staffId(generatedStaffId)
                                .role(request.getRole())
                                .isActive(true)
                                .mustChangePassword(true)
                                .build();

                UserEntity savedUser = userRepository.save(user);

                switch (savedUser.getRole()) {
                        case DOCTOR -> doctorProfileRepository.save(
                                        DoctorProfileEntity.builder()
                                                        .user(savedUser)
                                                        .build());

                        case RECEPTIONIST -> receptionistProfileRepository.save(
                                        ReceptionistProfile.builder()
                                                        .user(savedUser)
                                                        .build());

                        case PHARMACIST -> pharmacistProfileRepository.save(
                                        PharmacistProfileEntity.builder()
                                                        .user(savedUser)
                                                        .build());

                        case RADIOLOGIST -> radiologistProfileRepository.save(
                                        RadiologistEntity.builder()
                                                        .user(savedUser)
                                                        .build());

                        default -> {

                        }
                }

                UserSummaryResponse response = mapToResponse(savedUser);
                response.setTempPassword(rawTempPassword);
                return response;
        }

        @Override
        public List<UserSummaryResponse> getAllUsers() {
                return userRepository.findAll()
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Override
        public UserSummaryResponse getUserById(UUID id) {
                UserEntity user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User Not found"));
                return mapToResponse(user);
        }

        @Override
        public UserSummaryResponse updateStatus(UUID id, UpdateUserStatusRequest request) {
                UserEntity user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User Not found"));

                user.setIsActive(request.getIsActive());

                UserEntity updated = userRepository.save(user);

                return mapToResponse(updated);
        }

        @Override
        public UserSummaryResponse assignRole(UUID id, AssignRoleRequest request) {
                UserEntity user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with id: " + id));
                user.setRole(request.getRole());
                return mapToResponse(userRepository.save(user));
        }

        private String generateTempPassword() {
                return UUID.randomUUID().toString().substring(0, 8);
        }

        private UserSummaryResponse mapToResponse(UserEntity user) {
                return UserSummaryResponse.builder()
                                .userId(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .phoneNumber(user.getPhoneNumber())
                                .staffId(user.getStaffId())
                                .role(user.getRole())
                                .isActive(user.getIsActive())
                                .createdAt(user.getCreatedAt())
                                .build();
        }

}
