package com.itmo.microservices.demo.users.impl.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.demo.users.api.model.AppUserModel;
import com.itmo.microservices.demo.users.api.model.RegistrationRequest;
import com.itmo.microservices.demo.users.impl.entity.AppUser;
import com.itmo.microservices.demo.users.impl.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
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

        when(repository.findByUsername("username")).thenReturn(appUser);
        when(repository.save(any())).thenReturn(appUser);

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");


    }

    private final RegistrationRequest request = new RegistrationRequest("username", "name", "surname", "email", "password");

    @org.junit.Test
    public void registerTest() {
        userService.registerUser(request);
        AppUserModel user = new AppUserModel(
                "username",
                "name",
                "surname",
                "email",
                "password"
        );
        Assert.assertEquals(user.getName(), userService.getUser("username").getName());
        Assert.assertEquals(user.getSurname(), userService.getUser("username").getSurname());
        Assert.assertEquals(user.getEmail(), userService.getUser("username").getEmail());
        Assert.assertEquals(user.getPassword(), userService.getUser("username").getPassword());
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
        Assert.assertEquals(user.getName(), userService.getUser("username").getName());
        Assert.assertEquals(user.getSurname(), userService.getUser("username").getSurname());
        Assert.assertEquals(user.getEmail(), userService.getUser("username").getEmail());
        Assert.assertEquals(user.getPassword(), userService.getUser("username").getPassword());
        Assert.assertEquals(null, userService.getUser("anotherusername"));
    }
}
