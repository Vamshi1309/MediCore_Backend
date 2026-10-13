package com.vamshi.HospitalManagementSystem.receptionist.services;

import java.util.List;

import com.vamshi.HospitalManagementSystem.receptionist.dtos.ReceptionistProfileResponse;
import com.vamshi.HospitalManagementSystem.receptionist.dtos.UpdateReceptionistProfileRequest;
import com.vamshi.HospitalManagementSystem.admin.dtos.UserSummaryResponse;

public interface ReceptionistService {
    public ReceptionistProfileResponse getMyProfile();

    public ReceptionistProfileResponse updateMyProfile(UpdateReceptionistProfileRequest request);

    public List<UserSummaryResponse> getAllPatients();
}
