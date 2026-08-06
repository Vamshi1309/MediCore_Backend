package com.vamshi.HospitalManagementSystem.twilio.service;

public interface OtpService {

    void sendOtp(String phoneNumber);

    boolean verifyOtp(String phoneNumber, String otp);

}
