package com.rizalamar.financetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.model.auth.LoginUserRequest;
import com.rizalamar.financetracker.model.auth.RegisterUserRequest;
import com.rizalamar.financetracker.repository.TransactionRepository;
import com.rizalamar.financetracker.repository.UserRepository;
import com.rizalamar.financetracker.repository.WalletRepository;
import com.rizalamar.financetracker.utils.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAllInBatch();
        walletRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void registerSuccess() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setName("Test User");
        request.setUsername("testuser");
        request.setPassword("testuser123");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data").value("Registration successful, please login to access features")
                );
    }

    @Test
    void registterFailedDuplicate() throws Exception {
        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword(PasswordUtil.hashPassword("testuser123"));
        userRepository.saveAndFlush(user);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setName("Test User");
        request.setUsername("testuser");
        request.setPassword("testuser123");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("400 BAD_REQUEST \"Username already registered\"")
                );

    }

    @Test
    void registerFailedValidation() throws  Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setName("");
        request.setUsername("qa");
        request.setPassword("abc");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message", allOf(
                                containsString("Name is required"),
                                containsString("Username must be between 3-20 characters"),
                                containsString("Password must be at least 8 characters"),
                                containsString("Password must contain at least one number")
                        ))
                );
    }

    @Test
    void loginSuccess() throws Exception {
        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword(PasswordUtil.hashPassword("testuser123"));
        userRepository.saveAndFlush(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("testuser");
        request.setPassword("testuser123");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.token").isNotEmpty()
                );
    }

    @Test
    void loginWrongPassword() throws Exception {
        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword(PasswordUtil.hashPassword("testuser123"));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("testuser");
        request.setPassword("testuser111");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
                .andExpectAll(
                        status().isUnauthorized(),
                        jsonPath("$.message").value("401 UNAUTHORIZED \"Username or password wrong\"")
                );
    }

    @Test
    void loginUserNotFound() throws Exception{
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("testuser");
        request.setPassword("testuser123");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/api/auth/login")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpectAll(
                        status().isUnauthorized(),
                        jsonPath("$.message").value("401 UNAUTHORIZED \"User not found\"")
                );
    }
}