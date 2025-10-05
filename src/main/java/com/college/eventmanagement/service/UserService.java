package com.college.eventmanagement.service;

import com.college.eventmanagement.dto.AuthDtos;
import com.college.eventmanagement.entity.User;

public interface UserService {
    AuthDtos.UserResponse register(AuthDtos.RegisterRequest request);
    AuthDtos.AuthResponse login(AuthDtos.LoginRequest request);
    AuthDtos.AuthResponse refresh(AuthDtos.RefreshRequest request);
    User getCurrentUser();
}
