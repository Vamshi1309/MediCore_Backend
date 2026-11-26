package com.vamshi.HospitalManagementSystem.radiologist.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vamshi.HospitalManagementSystem.common.ApiResponse;
import com.vamshi.HospitalManagementSystem.radiologist.dtos.RadiologistProfileResponse;
import com.vamshi.HospitalManagementSystem.radiologist.dtos.UpdateRadiologistProfileRequest;
import com.vamshi.HospitalManagementSystem.radiologist.services.RadiologistService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/radiologist/profile")
@RequiredArgsConstructor
public class RadiologistController {

    private final RadiologistService radiologistService;

    @GetMapping
    public ResponseEntity<ApiResponse<RadiologistProfileResponse>> getProfile() {
        RadiologistProfileResponse response = radiologistService.getMyProfile();

        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<RadiologistProfileResponse>> updateMyProfile(
            @Valid @RequestBody UpdateRadiologistProfileRequest request) {
        RadiologistProfileResponse response = radiologistService.updateMyProfile(request);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }
}
