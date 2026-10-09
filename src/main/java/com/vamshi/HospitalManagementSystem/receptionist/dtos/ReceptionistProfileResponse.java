package com.vamshi.HospitalManagementSystem.receptionist.dtos;

import java.util.UUID;

import com.vamshi.HospitalManagementSystem.common.enums.Shifts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceptionistProfileResponse {
    private UUID userId;
    private String name;
    private String email;
    private String phoneNumber;
    private Shifts shift;
}
