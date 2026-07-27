package com.vamshi.HospitalManagementSystem.auth.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vamshi.HospitalManagementSystem.common.enums.Role;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;
import com.vamshi.HospitalManagementSystem.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

                UserEntity user = userRepository.findByPhoneNumber(identifier)
                                .or(() -> userRepository.findByStaffIdAndRoleNot(identifier, Role.PATIENT))
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));

                List<SimpleGrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

                String principal = user.getRole() == Role.PATIENT
                                ? user.getPhoneNumber()
                                : user.getStaffId();

                return new User(
                                principal,
                                user.getPassword(),
                                authorities);
        }
}
