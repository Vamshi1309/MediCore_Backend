package com.vamshi.HospitalManagementSystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.vamshi.HospitalManagementSystem.common.enums.Role;
import com.vamshi.HospitalManagementSystem.doctor.entities.DoctorProfileEntity;
import com.vamshi.HospitalManagementSystem.doctor.repositories.DoctorProfileRepository;
import com.vamshi.HospitalManagementSystem.patient.entities.PatientProfileEntity;
import com.vamshi.HospitalManagementSystem.patient.repositories.PatientProfileRepository;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;
import com.vamshi.HospitalManagementSystem.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientProfileRepository patientProfileRepository;

    @Value("${admin.phone}")
    private String adminPhone;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {

        seedAdmin();

        seedDoctors();

        seedReceptionists();

        seedPharmacists();

        seedRadiologists();

        seedPatients();

        log.info("✅ Seed data initialized");
    }

    private void saveIfNotExists(UserEntity user) {
        boolean exists;

        if (user.getRole() == Role.PATIENT) {
            exists = userRepository.existsByPhoneNumber(user.getPhoneNumber());
        } else {
            exists = userRepository.existsByStaffId(user.getStaffId());
        }

        if (!exists) {
            userRepository.save(user);

            log.info("Created {} - {}",
                    user.getRole(),
                    user.getName());
        }
    }

    private void seedAdmin() {
        saveIfNotExists(
                UserEntity.builder()
                        .name("Super Admin")
                        .staffId("ADMIN-0001")
                        .phoneNumber("9999999999")
                        .email("admin@hospital.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .isActive(true)
                        .build());
    }

    private void seedDoctors() {

        for (int i = 1; i <= 3; i++) {

            final int doctorNumber = i;

            String staffId = String.format("DOC-%04d", doctorNumber);

            UserEntity user = userRepository
                    .findByStaffId(staffId)
                    .orElseGet(() -> {

                        UserEntity newUser = UserEntity.builder()
                                .name("Doctor " + doctorNumber)
                                .staffId(staffId)
                                .phoneNumber(String.format("900000000%d", doctorNumber))
                                .email("doctor" + doctorNumber + "@hospital.com")
                                .password(passwordEncoder.encode("doctor123"))
                                .role(Role.DOCTOR)
                                .isActive(true)
                                .build();

                        UserEntity saved = userRepository.save(newUser);

                        log.info("Created DOCTOR - {}", saved.getName());

                        return saved;
                    });

            // Create profile if it doesn't already exist
            if (doctorProfileRepository.findByUserId(user.getId()).isEmpty()) {

                DoctorProfileEntity doctorProfile = DoctorProfileEntity.builder()
                        .user(user)
                        .specialization("Specialization " + doctorNumber)
                        .qualification("MBBS")
                        .experienceInYears(doctorNumber * 2)
                        .build();

                doctorProfileRepository.save(doctorProfile);

                log.info("Created doctor profile for {}", staffId);
            }
        }
    }

    private void seedReceptionists() {

        for (int i = 1; i <= 2; i++) {

            saveIfNotExists(

                    UserEntity.builder()
                            .name("Receptionist " + i)
                            .staffId(String.format("REC-%04d", i))
                            .phoneNumber(String.format("910000000%d", i))
                            .email("receptionist" + i + "@hospital.com")
                            .password(passwordEncoder.encode("reception123"))
                            .role(Role.RECEPTIONIST)
                            .isActive(true)
                            .build()

            );
        }
    }

    private void seedPharmacists() {

        for (int i = 1; i <= 2; i++) {

            saveIfNotExists(

                    UserEntity.builder()
                            .name("Pharmacist " + i)
                            .staffId(String.format("PHA-%04d", i))
                            .phoneNumber(String.format("920000000%d", i))
                            .email("pharmacist" + i + "@hospital.com")
                            .password(passwordEncoder.encode("pharmacy123"))
                            .role(Role.PHARMACIST)
                            .isActive(true)
                            .build()

            );
        }
    }

    private void seedRadiologists() {

        for (int i = 1; i <= 2; i++) {

            saveIfNotExists(

                    UserEntity.builder()
                            .name("Radiologist " + i)
                            .staffId(String.format("RAD-%04d", i))
                            .phoneNumber(String.format("930000000%d", i))
                            .email("radiologist" + i + "@hospital.com")
                            .password(passwordEncoder.encode("radiology123"))
                            .role(Role.RADIOLOGIST)
                            .isActive(true)
                            .build()

            );
        }
    }

    private void seedPatients() {

        for (int i = 1; i <= 5; i++) {

            final int patientNumber = i;

            String phoneNumber = String.format("940000000%d", patientNumber);

            UserEntity user = userRepository
                    .findByPhoneNumber(phoneNumber)
                    .orElseGet(() -> {

                        UserEntity newUser = UserEntity.builder()
                                .name("Patient " + patientNumber)
                                .phoneNumber(phoneNumber)
                                .email("patient" + patientNumber + "@gmail.com")
                                .password(passwordEncoder.encode("patient123"))
                                .role(Role.PATIENT)
                                .isActive(true)
                                .build();

                        UserEntity saved = userRepository.save(newUser);

                        log.info("Created PATIENT - {}", saved.getName());

                        return saved;
                    });

            if (patientProfileRepository.findByUserId(user.getId()).isEmpty()) {

                PatientProfileEntity patientProfile = PatientProfileEntity.builder()
                        .user(user)
                        .dateOfBirth("2000-01-01")
                        .bloodGroup("O+")
                        .emergencyContact("9000000000")
                        .insuranceInfo("Health Insurance")
                        .build();

                patientProfileRepository.save(patientProfile);

                log.info("Created patient profile for {}", user.getName());
            }
        }

        // Vamshi test patient
        String phoneNumber = "8639933075";

        UserEntity user = userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseGet(() -> {

                    UserEntity newUser = UserEntity.builder()
                            .name("vamshi")
                            .phoneNumber(phoneNumber)
                            .email("vamshi@hospital.com")
                            .password(passwordEncoder.encode("test@123"))
                            .role(Role.PATIENT)
                            .isActive(true)
                            .build();

                    UserEntity saved = userRepository.save(newUser);

                    log.info("Created PATIENT - {}", saved.getName());

                    return saved;
                });

        if (patientProfileRepository.findByUserId(user.getId()).isEmpty()) {

            PatientProfileEntity patientProfile = PatientProfileEntity.builder()
                    .user(user)
                    .dateOfBirth("2000-01-01")
                    .bloodGroup("O+")
                    .emergencyContact(phoneNumber)
                    .insuranceInfo("Health Insurance")
                    .build();

            patientProfileRepository.save(patientProfile);

            log.info("Created patient profile for {}", user.getName());
        }
    }
}
