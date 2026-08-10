package com.vamshi.HospitalManagementSystem.fast2sms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class Fast2SmsConfig {

    @Value("${fast2sms.api-key}")
    private String apiKey;

    @Value("${fast2sms.api-url}")
    private String apiUrl;
}
