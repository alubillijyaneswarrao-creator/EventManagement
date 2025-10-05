package com.college.eventmanagement.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;

public class AuthDtos {

    public static class RegisterRequest {
        @NotBlank
        public String fullName;
        @Email @NotBlank
        public String email;
        @NotBlank @Size(min = 6)
        public String password;
        public String phone;
    }

    public static class LoginRequest {
        @Email @NotBlank
        public String email;
        @NotBlank
        public String password;
    }

    public static class AuthResponse {
        public String accessToken;
        public String refreshToken;
        public String tokenType = "Bearer";
        public Long userId;
        public String role;
    }

    public static class RefreshRequest {
        @NotBlank
        public String refreshToken;
    }

    public static class UserResponse {
        public Long id;
        public String fullName;
        public String email;
        public String role;
        public String phone;
        public boolean enabled;
        public Instant createdAt = Instant.now();
    }
}
