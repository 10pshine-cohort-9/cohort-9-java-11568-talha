package com.tenpearls.contactmanager.service;

import com.tenpearls.contactmanager.dto.AuthResponse;
import com.tenpearls.contactmanager.dto.ChangePasswordRequest;
import com.tenpearls.contactmanager.dto.LoginRequest;
import com.tenpearls.contactmanager.dto.RegisterRequest;
import com.tenpearls.contactmanager.dto.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getCurrentUser(String username);
    void changePassword(String username, ChangePasswordRequest request);
}
