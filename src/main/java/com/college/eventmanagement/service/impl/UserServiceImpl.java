package com.college.eventmanagement.service.impl;

import com.college.eventmanagement.dto.AuthDtos;
import com.college.eventmanagement.entity.Role;
import com.college.eventmanagement.entity.User;
import com.college.eventmanagement.exception.ApiException;
import com.college.eventmanagement.repository.UserRepository;
import com.college.eventmanagement.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements com.college.eventmanagement.service.UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public AuthDtos.UserResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already registered");
        }
        User user = new User();
        user.setFullName(request.fullName);
        user.setEmail(request.email);
        user.setPasswordHash(passwordEncoder.encode(request.password));
        user.setPhone(request.phone);
        user.setRole(Role.ATTENDEE);
        userRepository.save(user);
        return toUserResponse(user);
    }

    @Override
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email, request.password)
        );
        var userDetails = userDetailsService.loadUserByUsername(request.email);
        User user = userRepository.findByEmail(request.email).orElseThrow();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("uid", user.getId());
        String access = jwtService.generateAccessToken(user.getEmail(), claims);
        String refresh = jwtService.generateRefreshToken(user.getEmail(), claims);
        AuthDtos.AuthResponse resp = new AuthDtos.AuthResponse();
        resp.accessToken = access;
        resp.refreshToken = refresh;
        resp.userId = user.getId();
        resp.role = user.getRole().name();
        return resp;
    }

    @Override
    public AuthDtos.AuthResponse refresh(AuthDtos.RefreshRequest request) {
        var jws = jwtService.parseToken(request.refreshToken);
        String email = jws.getBody().getSubject();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token"));
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("uid", user.getId());
        AuthDtos.AuthResponse resp = new AuthDtos.AuthResponse();
        resp.accessToken = jwtService.generateAccessToken(user.getEmail(), claims);
        resp.refreshToken = request.refreshToken; // keep existing
        resp.userId = user.getId();
        resp.role = user.getRole().name();
        return resp;
    }

    @Override
    public User getCurrentUser() {
        // This method is typically implemented using SecurityContextHolder
        // Kept simple: look up from SecurityContext
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow();
    }

    private AuthDtos.UserResponse toUserResponse(User user) {
        AuthDtos.UserResponse r = new AuthDtos.UserResponse();
        r.id = user.getId();
        r.fullName = user.getFullName();
        r.email = user.getEmail();
        r.role = user.getRole().name();
        r.phone = user.getPhone();
        r.enabled = user.isEnabled();
        return r;
    }
}
