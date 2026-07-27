package com.vamshi.HospitalManagementSystem.admin.services;

import org.springframework.stereotype.Service;

import com.vamshi.HospitalManagementSystem.common.enums.Role;
import com.vamshi.HospitalManagementSystem.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffIdGeneratorService {
    private final UserRepository userRepository;

    public String generateStaffId(Role role) {
        String prefix = getPrefix(role);

        long next = userRepository.countByStaffIdStartingWith(prefix + "-") + 1;

        String candidate;
        do {
            candidate = String.format("%s-%04d", prefix, next);
            next++;
        } while (userRepository.existsByStaffId(candidate));
        return candidate;

    }

    private String getPrefix(Role role) {
        switch (role) {
            case DOCTOR:
                return "MC-DOC";
            case RECEPTIONIST:
                return "MC-REC";
            case PHARMACIST:
                return "MC-PHM";
            case RADIOLOGIST:
                return "MC-RAD";
            case ADMIN:
                return "MC-ADM";
            case PATIENT:
                throw new IllegalArgumentException("Patients do not get a staff ID");
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}
