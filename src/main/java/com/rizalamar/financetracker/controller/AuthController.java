package com.rizalamar.financetracker.controller;

import com.rizalamar.financetracker.model.RegisterUserRequest;
import com.rizalamar.financetracker.model.WebResponse;
import com.rizalamar.financetracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/register")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
        authService.register(request);
        return WebResponse.<String>builder().data("Registration successful, please login to access features").build();
    }

}
