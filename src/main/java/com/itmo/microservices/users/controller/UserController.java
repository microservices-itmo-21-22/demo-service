package com.itmo.microservices.users.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.itmo.microservices.users.entity.AppUser;
import com.itmo.microservices.users.model.AppUserDto;
import com.itmo.microservices.users.model.RegistrationRequest;
import com.itmo.microservices.users.service.UserService;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public void register(@RequestBody RegistrationRequest request) {

    }

    @GetMapping("/me")
    public AppUserDto getCurrentUserInfo(@AuthenticationPrincipal AppUser user) {
        return null;
    }

    @DeleteMapping("/me")
    public void deleteCurrentUser(@AuthenticationPrincipal AppUser user) {

    }
}
