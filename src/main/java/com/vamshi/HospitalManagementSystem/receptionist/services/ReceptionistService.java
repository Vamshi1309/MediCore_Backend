package com.vamshi.HospitalManagementSystem.receptionist.services;

import com.vamshi.HospitalManagementSystem.receptionist.dtos.ReceptionistProfileResponse;
import com.vamshi.HospitalManagementSystem.receptionist.dtos.UpdateReceptionistProfileRequest;

public interface ReceptionistService {
    public ReceptionistProfileResponse getMyProfile();

    public ReceptionistProfileResponse updateMyProfile(UpdateReceptionistProfileRequest request);
}
