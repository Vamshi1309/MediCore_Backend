package com.vamshi.HospitalManagementSystem.fast2sms.service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.vamshi.HospitalManagementSystem.exceptions.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final String OTP_PREFIX = "otp:";
    private static final Duration OTP_EXPIRY = Duration.ofMinutes(5);

    private final Fast2SmsService fast2SmsService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void sendOtp(String phoneNumber) {

        if (Boolean.TRUE.equals(redisTemplate.hasKey(getOtpKey(phoneNumber)))) {
            throw new BadRequestException(
                    "An OTP has already been sent. Please wait 5 minutes or verify the existing OTP.");
        }

        String otp = generateOtp();

        redisTemplate.opsForValue().set(
                getOtpKey(phoneNumber),
                otp,
                OTP_EXPIRY);

        fast2SmsService.sendOtp(phoneNumber, otp);
    }

    @Override
    public boolean verifyOtp(String phoneNumber, String otp) {

        String cachedOtp = redisTemplate.opsForValue().get(getOtpKey(phoneNumber));

        if (cachedOtp == null) {
            return false;
        }

        if (!cachedOtp.equals(otp)) {
            return false;
        }

        redisTemplate.delete(getOtpKey(phoneNumber));

        return true;
    }

    private String generateOtp() {
        return String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private String getOtpKey(String phoneNumber) {
        return OTP_PREFIX + phoneNumber;
    }
}
