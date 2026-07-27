package com.vamshi.HospitalManagementSystem.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffLoginRequest {

    @NotBlank(message = "Staff ID is required")
    private String staffId;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be of 6 Characters")
    private String password;
}
