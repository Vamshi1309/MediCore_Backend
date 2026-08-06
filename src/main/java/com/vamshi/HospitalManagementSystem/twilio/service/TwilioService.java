package com.vamshi.HospitalManagementSystem.twilio.service;

public interface TwilioService {
    void sendSms(String phoneNumber, String message);
}
