package com.vamshi.HospitalManagementSystem.auth.security;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;
import com.vamshi.HospitalManagementSystem.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username)
                        throws UsernameNotFoundException {

                // Try phoneNumber (Patient login)
                Optional<UserEntity> byPhone = userRepository
                                .findByPhoneNumber(username);

                if (byPhone.isPresent()) {
                        return buildUserDetails(
                                        byPhone.get(),
                                        byPhone.get().getPhoneNumber());
                }

                // Try staffId (Staff login)
                Optional<UserEntity> byStaffId = userRepository
                                .findByStaffId(username);

                if (byStaffId.isPresent()) {
                        return buildUserDetails(
                                        byStaffId.get(),
                                        byStaffId.get().getStaffId());
                }

                throw new UsernameNotFoundException(
                                "User not found: " + username);
        }

        private UserDetails buildUserDetails(
                        UserEntity user, String username) {
                return new User(
                                username, // phone OR staffId
                                user.getPassword(),
                                List.of(new SimpleGrantedAuthority(
                                                "ROLE_" + user.getRole().name())));
        }
}
