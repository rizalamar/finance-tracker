package com.rizalamar.financetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.model.auth.LoginUserRequest;
import com.rizalamar.financetracker.model.user.UpdateUserResquest;
import com.rizalamar.financetracker.repository.TransactionRepository;
import com.rizalamar.financetracker.repository.UserRepository;
import com.rizalamar.financetracker.repository.WalletRepository;
import com.rizalamar.financetracker.security.JwtUtil;
import com.rizalamar.financetracker.utils.PasswordUtil;
import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

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
    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAllInBatch();
        walletRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword(PasswordUtil.hashPassword("testuser123"));
        userRepository.save(user);
    }

    @Test
    void getCurrentUser() throws Exception {
        String token = jwtUtil.generateToken("testuser");

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.username").value("testuser"),
                        jsonPath("$.data.name").value("Test User")
                );
    }

    @Test
    void getCurrentUserNoToken() throws Exception {
        mockMvc.perform(
                        get("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void getCurrentUserInvalidToken() throws Exception {
        mockMvc.perform(
                        get("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer token-salah")
                )
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void updateCurrentUserName() throws Exception {
        String token = jwtUtil.generateToken("testuser");

        UpdateUserResquest request = new UpdateUserResquest();
        request.setName("New Test User");
        request.setPassword("testuser");

        String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(
                        patch("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer "  + token)
                                .content(requestJson)
                )
                        .andExpectAll(
                                status().isOk()
                        );
    }

    @Test
    void updateCurrentUserPassword() throws Exception {
        String token = jwtUtil.generateToken("testuser");

        UpdateUserResquest request = new UpdateUserResquest();
        request.setName("Test User");
        request.setPassword("testuser1234");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        patch("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer "  + token)
                                .content(requestJson)
                )
                .andExpectAll(
                        status().isOk()
                );
    }
}