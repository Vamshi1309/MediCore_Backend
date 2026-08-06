package com.vamshi.HospitalManagementSystem.twilio.service;

import org.springframework.stereotype.Service;

import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.vamshi.HospitalManagementSystem.exceptions.TwilioException;
import com.vamshi.HospitalManagementSystem.twilio.config.TwilioConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TwilioServiceImpl implements TwilioService {

    private final TwilioConfig twilioConfig;

    @Override
    public void sendSms(String phoneNumber, String message) {

        try {

            Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(twilioConfig.getPhoneNumber()),
                    message).create();

        } catch (ApiException ex) {

            throw new TwilioException(
                    "Unable to send OTP. Please try again later.",
                    ex);

        }
    }
}
