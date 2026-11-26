package com.vamshi.HospitalManagementSystem.radiologist.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRadiologistProfileRequest {

    private String name;

    private String phoneNumber;

    private String labSection;

    private String certification;

}
