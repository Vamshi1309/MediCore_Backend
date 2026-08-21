package com.vamshi.HospitalManagementSystem.common.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.vamshi.HospitalManagementSystem.exceptions.ResourceNotFoundException;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;
import com.vamshi.HospitalManagementSystem.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthUtil {

        private final UserRepository userRepository;

        public UserEntity getLoggedInUser() {

                String username = SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getName();

                System.out.println("================= Debug =====================");
                System.out.println("UserName:-" + username);
                System.out.println("================= Debug =====================");

                // Try phoneNumber first (Patient)
                return userRepository
                                .findByPhoneNumber(username)
                                .orElseGet(() -> userRepository
                                                .findByStaffId(username)
                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                                "User not found")));
        }
}
