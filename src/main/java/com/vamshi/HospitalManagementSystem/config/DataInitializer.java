package com.vamshi.HospitalManagementSystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.vamshi.HospitalManagementSystem.common.enums.Role;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;
import com.vamshi.HospitalManagementSystem.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

            saveIfNotExists(

                    UserEntity.builder()
                            .name("Doctor " + i)
                            .staffId(String.format("DOC-%04d", i))
                            .phoneNumber(String.format("900000000%d", i))
                            .email("doctor" + i + "@hospital.com")
                            .password(passwordEncoder.encode("doctor123"))
                            .role(Role.DOCTOR)
                            .isActive(true)
                            .build()

            );
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

            saveIfNotExists(

                    UserEntity.builder()
                            .name("Patient " + i)
                            .phoneNumber(String.format("940000000%d", i))
                            .email("patient" + i + "@gmail.com")
                            .password(passwordEncoder.encode("patient123"))
                            .role(Role.PATIENT)
                            .isActive(true)
                            .build()

            );
        }

        saveIfNotExists(
                UserEntity.builder()
                        .name("vamshi")
                        .phoneNumber("8639933075")
                        .email("vamshi@hospital.com")
                        .password(passwordEncoder.encode("test@123"))
                        .role(Role.PATIENT)
                        .isActive(true)
                        .build());
    }
}
