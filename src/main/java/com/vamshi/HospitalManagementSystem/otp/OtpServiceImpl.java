package com.vamshi.HospitalManagementSystem.otp;

import java.time.Duration;
import java.util.Random;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final StringRedisTemplate redisTemplate;

    private static final String OTP_PREFIX = "otp:";

    @Override
    public void sendOtp(String phoneNumber) {

        int otp = 100000 + new Random().nextInt(900000);

        String key = OTP_PREFIX + phoneNumber;

        redisTemplate.opsForValue().set(
                key,
                String.valueOf(otp),
                Duration.ofMinutes(5));

        System.out.println("================================");
        System.out.println("OTP for " + phoneNumber + " : " + otp);
        System.out.println("OTP expires in 5 minutes");
        System.out.println("================================");
    }

    @Override
    public boolean verifyOtp(String phoneNumber, String otp) {

        String key = OTP_PREFIX + phoneNumber;

        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null) {
            return false;
        }

        if (!storedOtp.equals(otp)) {
            return false;
        }

        // OTP is valid → delete it so it cannot be reused
        redisTemplate.delete(key);

        return true;
    }
}
