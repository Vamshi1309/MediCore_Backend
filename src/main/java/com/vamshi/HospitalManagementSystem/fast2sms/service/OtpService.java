package com.vamshi.HospitalManagementSystem.fast2sms.service;

public interface OtpService {

    void sendOtp(String phoneNumber);

    boolean verifyOtp(String phoneNumber, String otp);

}
