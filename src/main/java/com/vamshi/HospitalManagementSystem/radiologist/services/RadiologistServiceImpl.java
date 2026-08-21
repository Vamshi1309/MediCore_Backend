package com.vamshi.HospitalManagementSystem.radiologist.services;

import org.springframework.stereotype.Service;
	import com.vamshi.HospitalManagementSystem.common.utils.AuthUtil;import com.vamshi.HospitalManagementSystem.exceptions.ResourceNotFoundException;
import com.vamshi.HospitalManagementSystem.radiologist.dtos.RadiologistProfileResponse;
import com.vamshi.HospitalManagementSystem.radiologist.dtos.UpdateRadiologistProfileRequest;
import com.vamshi.HospitalManagementSystem.radiologist.entities.RadiologistEntity;
import com.vamshi.HospitalManagementSystem.radiologist.repositories.RadiologistProfileRepository;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RadiologistServiceImpl implements RadiologistService {

    private final RadiologistProfileRepository radiologistRepository;

    private final AuthUtil authUtil;

    @Override
    public RadiologistProfileResponse getMyProfile() {
        UserEntity user = authUtil.getLoggedInUser();

        RadiologistEntity radiologist = radiologistRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Radiologist not found with that Id"));

        return mapToResponse(radiologist);
    }

    @Override
    public RadiologistProfileResponse updateMyProfile(UpdateRadiologistProfileRequest request) {
        UserEntity user = authUtil.getLoggedInUser();

        RadiologistEntity radiologist = radiologistRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Radiologist not found with that Id"));

        if (request.getCertification() != null) {
            radiologist.setCertification(request.getCertification());
        }

        if (request.getLabSection() != null) {
            radiologist.setLabSection(request.getLabSection());
        }

        RadiologistEntity saved = radiologistRepository.save(radiologist);

        return mapToResponse(saved);
    }

    private RadiologistProfileResponse mapToResponse(RadiologistEntity radiologist) {
        return RadiologistProfileResponse.builder()
                .userId(radiologist.getUser().getId())
                .name(radiologist.getUser().getName())
                .phoneNumber(radiologist.getUser().getPhoneNumber())
                .email(radiologist.getUser().getEmail())
                .certification(radiologist.getCertification())
                .labSection(radiologist.getLabSection())
                .build();
    }

}
