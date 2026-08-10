package com.vamshi.HospitalManagementSystem.fast2sms.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.vamshi.HospitalManagementSystem.exceptions.Fast2SmsException;
import com.vamshi.HospitalManagementSystem.fast2sms.config.Fast2SmsConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Fast2SmsServiceImpl implements Fast2SmsService {

    private final Fast2SmsConfig fast2SmsConfig;
    private final RestTemplate restTemplate;

    @Override
    public void sendOtp(String phoneNumber, String otp) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization", fast2SmsConfig.getApiKey());
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("variables_values", otp);
            body.add("route", "otp");
            body.add("numbers", phoneNumber); // 10-digit, no +91

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    fast2SmsConfig.getApiUrl(), request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()
                    || (response.getBody() != null && response.getBody().contains("\"return\":false"))) {
                throw new Fast2SmsException(
                        "Fast2SMS failed to send OTP: " + response.getBody(), null);
            }

        } catch (RestClientException ex) {
            throw new Fast2SmsException(
                    "Unable to send OTP. Please try again later.", ex);
        }
    }
}
