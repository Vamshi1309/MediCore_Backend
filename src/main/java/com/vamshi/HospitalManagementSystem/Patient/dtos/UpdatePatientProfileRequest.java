package com.vamshi.HospitalManagementSystem.patient.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePatientProfileRequest {

    private String name;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String bloodGroup;
    private String emergencyContact;
    private String insuranceInfo;
}