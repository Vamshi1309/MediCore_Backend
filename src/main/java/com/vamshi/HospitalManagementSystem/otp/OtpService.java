package com.vamshi.HospitalManagementSystem.otp;

public interface OtpService {

    void sendOtp(String phoneNumber);

    boolean verifyOtp(String phoneNumber, String otp);

}
