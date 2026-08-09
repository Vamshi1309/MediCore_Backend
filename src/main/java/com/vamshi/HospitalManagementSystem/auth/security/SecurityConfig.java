package com.vamshi.HospitalManagementSystem.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtFilter jwtFilter;

        private final UserDetailsServiceImpl userDetailsServiceImpl;

        private final CustomAuthenticationEntryPoint authEntryPoint;
        private final CustomAccessDeniedHandler accessDeniedHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))
                                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**")
                                                .permitAll()
                                                .requestMatchers("/api/admin/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/appointments")
                                                .hasAnyRole("RECEPTIONIST", "PATIENT")

                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/appointments")
                                                .hasAnyRole("RECEPTIONIST", "ADMIN")

                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/appointments/doctor/**")
                                                .hasRole("DOCTOR")

                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/appointments/patient/**")
                                                .hasAnyRole("PATIENT", "DOCTOR", "RECEPTIONIST")

                                                .requestMatchers(HttpMethod.PATCH,
                                                                "/api/appointments/*/status")
                                                .hasAnyRole("DOCTOR", "RECEPTIONIST")

                                                // Prescriptions
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/prescriptions")
                                                .hasRole("DOCTOR")

                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/prescriptions/*/download")
                                                .hasAnyRole("DOCTOR", "PATIENT", "PHARMACIST")

                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/prescriptions/doctor/**")
                                                .hasRole("DOCTOR")

                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/prescriptions/patient/**")
                                                .hasAnyRole("PATIENT", "DOCTOR", "PHARMACIST")

                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/prescriptions/**")
                                                .hasAnyRole("DOCTOR", "PATIENT", "PHARMACIST")

                                                // Lab Reports
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/lab-reports")
                                                .hasRole("RADIOLOGIST")

                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/lab-reports/**")
                                                .hasAnyRole("RADIOLOGIST", "DOCTOR",
                                                                "PATIENT", "ADMIN")
                                                .requestMatchers("/api/inventory/**")
                                                .hasAnyRole("PHARMACIST", "ADMIN")
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/pharmacy/dispense")
                                                .hasRole("PHARMACIST")

                                                .requestMatchers("/api/receptionist/**")
                                                .hasRole("RECEPTIONIST")

                                                .requestMatchers("/api/pharmacist/**")
                                                .hasRole("PHARMACIST")

                                                .requestMatchers("/api/patient/**")
                                                .hasRole("PATIENT")

                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/pharmacy/history/**")
                                                .hasAnyRole("PHARMACIST", "PATIENT",
                                                                "DOCTOR", "ADMIN")
                                                .requestMatchers("/api/doctor/**")
                                                .hasRole("DOCTOR")
                                                .anyRequest().authenticated())
                                .addFilterBefore(
                                                jwtFilter,
                                                UsernamePasswordAuthenticationFilter.class)
                                .authenticationProvider(authenticationProvider());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

                provider.setUserDetailsService(userDetailsServiceImpl);
                provider.setPasswordEncoder(passwordEncoder());

                return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
