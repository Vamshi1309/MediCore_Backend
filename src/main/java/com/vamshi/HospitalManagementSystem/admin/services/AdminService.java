package com.vamshi.HospitalManagementSystem.admin.services;

import java.util.List;
import java.util.UUID;

import com.vamshi.HospitalManagementSystem.admin.dtos.AdminStatsResponse;
import com.vamshi.HospitalManagementSystem.admin.dtos.CreateStaffRequest;
import com.vamshi.HospitalManagementSystem.admin.dtos.StaffSummaryResponse;
import com.vamshi.HospitalManagementSystem.admin.dtos.UpdateUserStatusRequest;

public interface AdminService {
    StaffSummaryResponse createStaff(CreateStaffRequest request);

    List<StaffSummaryResponse> getAllStaff();

    StaffSummaryResponse getStaffById(UUID id);

    StaffSummaryResponse updateStaffStatus(UUID id, UpdateUserStatusRequest request);

    AdminStatsResponse getStats();
}
