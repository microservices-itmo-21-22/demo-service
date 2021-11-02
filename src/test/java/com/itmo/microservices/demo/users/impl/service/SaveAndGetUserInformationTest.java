package com.itmo.microservices.demo.users.impl.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.demo.users.api.model.AppUserModel;
import com.itmo.microservices.demo.users.api.model.RegistrationRequest;
import com.itmo.microservices.demo.users.api.service.UserService;
import com.itmo.microservices.demo.users.impl.entity.AppUser;
import com.itmo.microservices.demo.users.impl.repository.UserRepository;
import com.itmo.microservices.demo.users.impl.service.DefaultUserService;
import com.itmo.microservices.demo.users.impl.service.JwtTokenManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.constraints.NotNull;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

@SuppressWarnings("UnstableApiUsage")
@RunWith(MockitoJUnitRunner.class)
public class SaveAndGetUserInformationTest {
    private AppUser appUser;

    @InjectMocks
    DefaultUserService userService;

    @Mock
    UserRepository repository;
    @Mock
    JwtTokenManager tokenManager;
    @Mock
    EventBus eventBus;
    @Mock
    PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        appUser = new AppUser(
                "username",
                "name",
                "surname",
                "email",
                "password"
        );

        //repository = mock(UserRepository.class);
        when(repository.findById("username")).thenReturn(Optional.of(appUser));
        when(repository.save(any())).thenReturn(appUser);

        //var passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        //when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        //var eventBus = mock(EventBus.class);
        //var tokenManager = mock(JwtTokenManager.class);
        //when(tokenManager.generateToken(any())).thenReturn("token");
        //when(tokenManager.generateRefreshToken(any())).thenReturn("refreshToken");

        //var eventLogger = mock(EventLogger.class);

        //userService = new DefaultUserService(repository, passwordEncoder, eventBus, tokenManager);


    }

    private final RegistrationRequest request = new RegistrationRequest("username", "name", "surname", "email", "password");

    @org.junit.Test
    public void registerTest(){
        userService.registerUser(request);
        AppUserModel user = new AppUserModel(
                "username",
                "name",
                "surname",
                "email",
                "password"
        );
        Assert.assertEquals(user, userService.findUser("username"));
    }

    @org.junit.Test
    public void findUserTest() {
        AppUserModel user = new AppUserModel(
                "username",
                "name",
                "surname",
                "email",
                "password"
        );
        Assert.assertEquals(user, userService.findUser("username"));
        Assert.assertEquals(null, userService.findUser("anotherusername"));
    }
}
