package com.rizalamar.financetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.entity.Wallet;
import com.rizalamar.financetracker.model.wallet.CreateWalletRequest;
import com.rizalamar.financetracker.model.wallet.UpdateWalletRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        walletRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testuser123");
        user.setName("Test User");
        userRepository.save(user);
    }

    @Test
    void createWalletSuccess() throws Exception {
        String token = jwtUtil.generateToken("testuser");

        CreateWalletRequest request = new CreateWalletRequest();
        request.setName("Tabungan Utama");
        request.setBalance(1000000L);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/wallets")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(requestJson)
        )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.name").value("Tabungan Utama"),
                        jsonPath("$.data.balance").value(1000000L)
                );
    }

    @Test
    void getWalletNotFoundFromOtherUser() throws Exception {
        User otherUser = new User();
        otherUser.setName("Other");
        otherUser.setUsername("other");
        otherUser.setPassword(PasswordUtil.hashPassword("secret123"));
        userRepository.save(otherUser);

        Wallet otherWallet = new Wallet();
        otherWallet.setName("Dompet Bersama");
        otherWallet.setBalance(5000000L);
        otherWallet.setUser(otherUser);
        walletRepository.save(otherWallet);

        String token = jwtUtil.generateToken("testuser");

        mockMvc.perform(
                get("/api/wallets/" + otherWallet.getId())
                        .header("Authorization", "Bearer " + token)
        )
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    void listWallet() throws Exception {
        User user = userRepository.findFirstByUsername("testuser").orElseThrow();

        Wallet wallet = new Wallet();
        wallet.setName("Dompet Anak");
        wallet.setBalance(3000000L);
        wallet.setUser(user);
        walletRepository.save(wallet);

        Wallet otherWallet = new Wallet();
        otherWallet.setName("Dompet Bersama");
        otherWallet.setBalance(5000000L);
        otherWallet.setUser(user);
        walletRepository.save(otherWallet);

        String token = jwtUtil.generateToken("testuser");

        mockMvc.perform(
                get("/api/wallets")
                        .header("Authorization", "Bearer " + token)
        )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data").isArray()
                );
    }

    @Test
    void walletUpdate() throws Exception {
        User user = userRepository.findFirstByUsername("testuser").orElseThrow();

        Wallet wallet = new Wallet();
        wallet.setName("Dompet Anak");
        wallet.setBalance(3000000L);
        wallet.setUser(user);
        walletRepository.save(wallet);

        String token = jwtUtil.generateToken("testuser");

        UpdateWalletRequest request = new UpdateWalletRequest();
        request.setName("Dompet Rizal");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                patch("/api/wallets/" + wallet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(requestJson)
        )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.name").value("Dompet Rizal")
                );
    }
}