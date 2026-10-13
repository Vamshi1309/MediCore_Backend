package com.vamshi.HospitalManagementSystem.receptionist.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vamshi.HospitalManagementSystem.common.utils.AuthUtil;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceNotFoundException;
import com.vamshi.HospitalManagementSystem.receptionist.dtos.ReceptionistProfileResponse;
import com.vamshi.HospitalManagementSystem.receptionist.dtos.UpdateReceptionistProfileRequest;
import com.vamshi.HospitalManagementSystem.receptionist.entities.ReceptionistProfile;
import com.vamshi.HospitalManagementSystem.receptionist.repositories.ReceptionistProfileRepository;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;
import com.vamshi.HospitalManagementSystem.user.repositories.UserRepository;
import com.vamshi.HospitalManagementSystem.admin.dtos.UserSummaryResponse;
import com.vamshi.HospitalManagementSystem.common.enums.Role;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReceptionistServiceImpl implements ReceptionistService {

        private final AuthUtil authUtil;

        private final ReceptionistProfileRepository receptionistRepository;

        private final UserRepository userRepository;

        @Override
        public ReceptionistProfileResponse getMyProfile() {

                UserEntity user = authUtil.getLoggedInUser();

                ReceptionistProfile receptionist = receptionistRepository
                                .findByUserId(user.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));

                return mapToResponse(receptionist);
        }

        @Transactional
        @Override
        public ReceptionistProfileResponse updateMyProfile(
                        UpdateReceptionistProfileRequest request) {

                UserEntity user = authUtil.getLoggedInUser();

                ReceptionistProfile receptionist = receptionistRepository
                                .findByUserId(user.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Receptionist profile not found"));

                if (request.getName() != null)
                        user.setName(request.getName());

                if (request.getEmail() != null)
                        user.setEmail(request.getEmail());

                if (request.getPhoneNumber() != null)
                        user.setPhoneNumber(request.getPhoneNumber());

                userRepository.save(user);

                return mapToResponse(receptionist);
        }

        private ReceptionistProfileResponse mapToResponse(
                        ReceptionistProfile receptionist) {

                return ReceptionistProfileResponse.builder()
                                .userId(receptionist.getUser().getId())
                                .name(receptionist.getUser().getName())
                                .email(receptionist.getUser().getEmail())
                                .phoneNumber(receptionist.getUser().getPhoneNumber())
                                .shift(receptionist.getShift() != null
                                                ? receptionist.getShift()
                                                : null)
                                .build();
        }

        @Override
        public List<UserSummaryResponse> getAllPatients() {
                List<UserEntity> patients = userRepository.findAllByRole(Role.PATIENT);

                return patients.stream()
                                .map(user -> UserSummaryResponse.builder()
                                                .userId(user.getId())
                                                .name(user.getName())
                                                .email(user.getEmail())
                                                .phoneNumber(user.getPhoneNumber())
                                                .staffId(user.getStaffId())
                                                .role(user.getRole())
                                                .isActive(user.getIsActive())
                                                .createdAt(user.getCreatedAt())
                                                .build())
                                .toList();
        }

}