package com.vamshi.HospitalManagementSystem.exceptions;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.vamshi.HospitalManagementSystem.common.ApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(ResourceAlreadyExistsException.class)
        public ResponseEntity<ApiResponse<?>> handleAlreadyExists(
                        ResourceAlreadyExistsException ex) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<?>> handleNotFound(
                        ResourceNotFoundException ex) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiResponse<?>> handleBadCredentials(
                        BadCredentialsException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.error("Invalid email or password"));
        }

        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<ApiResponse<?>> handleUsernameNotFound(
                        UsernameNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse<?>> handleDuplicate(
                        BadRequestException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(UnauthorizedRoleException.class)
        public ResponseEntity<ApiResponse<?>> handleRoleMismatch(
                        UnauthorizedRoleException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        // 400 - @Valid annotation failures on DTOs
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<?>> handleValidation(
                        MethodArgumentNotValidException ex) {
                String message = ex.getBindingResult()
                                .getFieldErrors()
                                .get(0)
                                .getDefaultMessage();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(message));
        }

        @ExceptionHandler(Fast2SmsException.class)
        public ResponseEntity<ApiResponse<?>> handleTwilioException(
                        Fast2SmsException ex) {

                log.error("Twilio Exception: ", ex);

                return ResponseEntity
                                .status(HttpStatus.BAD_GATEWAY)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

                Throwable cause = ex.getCause();

                if (cause instanceof InvalidFormatException ife) {
                        String fieldName = ife.getPath().isEmpty()
                                        ? "field"
                                        : ife.getPath().get(0).getFieldName();
                        String invalidValue = String.valueOf(ife.getValue());

                        if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                                String acceptedValues = Arrays.stream(ife.getTargetType().getEnumConstants())
                                                .map(Object::toString)
                                                .collect(Collectors.joining(", "));

                                String message = String.format(
                                                "Invalid value '%s' for field '%s'. Accepted values are: [%s]",
                                                invalidValue, fieldName, acceptedValues);
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(ApiResponse.error(message));
                        }
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error("Invalid value '" + invalidValue + "' for field '"
                                                        + fieldName + "'"));
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Malformed JSON request"));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<?>> handleGeneric(
                        Exception ex) {
                log.error("Unhandled exception: ", ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ex.getMessage()));
        }

}
