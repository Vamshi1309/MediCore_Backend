package com.vamshi.HospitalManagementSystem.pharmacist.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePharmacistProfileRequest {

    private String name;

    private String email;

    private String phoneNumber;

    private String licenseNumber;
}
