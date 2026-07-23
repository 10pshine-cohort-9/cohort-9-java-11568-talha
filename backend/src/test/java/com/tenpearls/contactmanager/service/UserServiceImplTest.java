package com.tenpearls.contactmanager.service;

import com.tenpearls.contactmanager.dto.*;
import com.tenpearls.contactmanager.exception.BadRequestException;
import com.tenpearls.contactmanager.exception.ResourceAlreadyExistsException;
import com.tenpearls.contactmanager.exception.UnauthorizedException;
import com.tenpearls.contactmanager.model.User;
import com.tenpearls.contactmanager.repository.UserRepository;
import com.tenpearls.contactmanager.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .phone("1234567890")
                .password("encodedPassword")
                .build();

        registerRequest = new RegisterRequest("test@example.com", "1234567890", "password123");
        loginRequest = new LoginRequest("test@example.com", "password123");
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse response = userService.register(registerRequest);

        assertNotNull(response);
        assertEquals(sampleUser.getEmail(), response.getEmail());
        assertEquals(sampleUser.getPhone(), response.getPhone());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_Failure_MissingEmailAndPhone() {
        RegisterRequest invalidRequest = new RegisterRequest("", "", "password123");

        assertThrows(BadRequestException.class, () -> userService.register(invalidRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_Failure_DuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("mockToken");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleUser));

        AuthResponse response = userService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals(sampleUser.getEmail(), response.getEmail());
        assertEquals(sampleUser.getId(), response.getId());
    }

    @Test
    void changePassword_Success() {
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest("password123", "newPassword123");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        userService.changePassword("test@example.com", passwordRequest);

        verify(userRepository, times(1)).save(sampleUser);
        assertEquals("newEncodedPassword", sampleUser.getPassword());
    }

    @Test
    void changePassword_Failure_IncorrectCurrentPassword() {
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest("wrongPassword", "newPassword123");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userService.changePassword("test@example.com", passwordRequest));
        verify(userRepository, never()).save(any(User.class));
    }
}
