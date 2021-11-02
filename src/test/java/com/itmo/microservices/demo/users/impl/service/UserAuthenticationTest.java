package com.itmo.microservices.demo.users.impl.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.demo.common.exception.NotFoundException;
import com.itmo.microservices.demo.users.api.model.AuthenticationRequest;
import com.itmo.microservices.demo.users.api.service.UserService;
import com.itmo.microservices.demo.users.impl.entity.AppUser;
import com.itmo.microservices.demo.users.impl.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("UnstableApiUsage")
public class UserAuthenticationTest {
    UserRepository userRepository;
    UserService userService;
    AppUser appUser;

    @BeforeEach
    public void setUp() {
        appUser = new AppUser(
                "username",
                "name",
                "surname",
                "email",
                "password"
        );

        userRepository = mock(UserRepository.class);
        when(userRepository.findByUsername("username")).thenReturn(appUser);

        var passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        var eventBus = mock(EventBus.class);
        var tokenManager = mock(JwtTokenManager.class);
        when(tokenManager.generateToken(any())).thenReturn("token");
        when(tokenManager.generateRefreshToken(any())).thenReturn("refreshToken");

        userService = new DefaultUserService(userRepository, passwordEncoder, eventBus, tokenManager);
    }

    @Test
    public void authenticateTest() {
        var authenticateResult = userService.authenticate(new AuthenticationRequest("username", "password"));

        assertEquals("token", authenticateResult.getAccessToken());
        assertEquals("refreshToken", authenticateResult.getRefreshToken());
    }

    @Test
    public void wrongAuthenticateTest() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.authenticate(new AuthenticationRequest("wrongUsername", "wrongPassword"));
        });
    }
}
