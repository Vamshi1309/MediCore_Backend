package com.vamshi.HospitalManagementSystem.user.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vamshi.HospitalManagementSystem.common.enums.Role;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByphoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByStaffId(String staffId);

    long countByStaffIdStartingWith(String prefix);

    Optional<UserEntity> findByPhoneNumberAndRole(String phoneNumber, Role role);

    Optional<UserEntity> findByStaffIdAndRoleNot(String staffId, Role excludedRole);
}
