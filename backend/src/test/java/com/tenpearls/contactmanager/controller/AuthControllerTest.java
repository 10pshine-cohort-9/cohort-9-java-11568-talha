package com.tenpearls.contactmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpearls.contactmanager.dto.*;
import com.tenpearls.contactmanager.security.CustomUserDetailsService;
import com.tenpearls.contactmanager.security.JwtTokenProvider;
import com.tenpearls.contactmanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters to isolate endpoint logic and mapping tests
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper is a plain Java object — no need for Spring injection here
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserResponse userResponse;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("user@example.com", "1234567890", "password123");
        loginRequest = new LoginRequest("user@example.com", "password123");
        userResponse = new UserResponse(1L, "user@example.com", "1234567890", null);
        authResponse = new AuthResponse("mockJwtToken", "Bearer", 1L, "user@example.com", "1234567890");
    }

    @Test
    void register_Success() throws Exception {
        when(userService.register(any(RegisterRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("user@example.com"));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void register_ValidationError_ShortPassword() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest("user@example.com", "1234567890", "123"); // Password < 6 chars

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void login_Success() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockJwtToken"))
                .andExpect(jsonPath("$.id").value(1L));

        verify(userService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void getCurrentUser_Success() throws Exception {
        when(userService.getCurrentUser("user@example.com")).thenReturn(userResponse);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@example.com");

        mockMvc.perform(get("/api/auth/me")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("user@example.com"));

        verify(userService, times(1)).getCurrentUser("user@example.com");
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void changePassword_Success() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password123", "newPassword123");
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@example.com");

        mockMvc.perform(post("/api/auth/change-password")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));

        verify(userService, times(1)).changePassword(eq("user@example.com"), any(ChangePasswordRequest.class));
    }
}
