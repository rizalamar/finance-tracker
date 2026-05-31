package com.rizalamar.financetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rizalamar.financetracker.entity.CategoryType;
import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.entity.Wallet;
import com.rizalamar.financetracker.model.category.CreateCategoryRequest;
import com.rizalamar.financetracker.repository.CategoryRepository;
import com.rizalamar.financetracker.repository.UserRepository;
import com.rizalamar.financetracker.repository.WalletRepository;
import com.rizalamar.financetracker.security.JwtUtil;
import com.rizalamar.financetracker.utils.PasswordUtil;
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
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAllInBatch();
        walletRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        User user = new User();
        user.setUsername("testuser");
        user.setPassword(PasswordUtil.hashPassword("testuser123"));
        user.setName("Test User");
        userRepository.save(user);
    }

    /**
     *
     *   2. Get Category
     *    * Success: Ambil satu kategori pakai ID-nya.
     *    * Failed (Not Found): Coba ambil pakai ID ngawur, atau ambil ID kategori milik User Lain.
     *      Pastikan hasilnya 404 Not Found.
     *
     *   3. List Category
     *    * Success: Buat 3 kategori (Makan, Gaji, Transport). Panggil list, pastikan panjang
     *      array-nya benar (3).
     *
     *   4. Update Category
     *    * Success: Ubah nama "Makan" jadi "Kuliner".
     *    * Success (Partial Update): Hanya ubah type-nya saja tanpa ubah nama.
     *
     *   5. Delete Category
     *    * Success: Hapus kategori, lalu coba get lagi, pastikan sudah tidak ada (404).
     */


    /**
     * 1. Create Category (Skenario Utama)
     *    * Success: Masukkan nama "Makan" dan type "EXPENSE". Pastikan status 200 OK dan data
     *      tersimpan.
     *    * Failed (Validation): Coba kirim nama kosong. Pastikan kena 400 Bad Request.
     *    * Failed (Duplicate): Ini yang penting! Buat kategori "Makan", lalu coba buat lagi dengan
     *      nama yang sama ("Makan"). Pastikan sistem menolak dengan pesan "Category name already
     *      exists".
     */
    @Test
    void createSuccess() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Makanan");
        request.setCategoryType(CategoryType.EXPENSE);

        String token = jwtUtil.generateToken("testuser");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/categories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(requestJson)
        )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.id").exists(),
                        jsonPath("$.data.name").value("Makanan"),
                        jsonPath("$.data.categoryType").value(CategoryType.EXPENSE.name())
                );
    }

    @Test
    void list() {
    }

    @Test
    void get() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}