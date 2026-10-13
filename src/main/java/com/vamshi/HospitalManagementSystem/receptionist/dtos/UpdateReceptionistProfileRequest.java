package com.vamshi.HospitalManagementSystem.receptionist.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReceptionistProfileRequest {

    private String name;

    private String email;

    private String phoneNumber;
}