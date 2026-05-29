package com.rizalamar.financetracker.service;

import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.model.auth.LoginUserRequest;
import com.rizalamar.financetracker.model.auth.RegisterUserRequest;
import com.rizalamar.financetracker.model.auth.TokenResponse;
import com.rizalamar.financetracker.repository.UserRepository;
import com.rizalamar.financetracker.security.JwtUtil;
import com.rizalamar.financetracker.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final JwtUtil jwtUtil;

    @Transactional
    public void register(RegisterUserRequest request) {
        validationService.validate(request);

        if (userRepository.findFirstByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
        user.setName(request.getName());

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginUserRequest request) {
        validationService.validate(request);

        // 1. find the user
        User user = userRepository.findFirstByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong"));

        boolean checkedPassword = PasswordUtil.checkPassword(request.getPassword(), user.getPassword());

        if (checkedPassword) {
            String token = jwtUtil.generateToken(user.getUsername());
            return TokenResponse.builder().token(token).build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong");
        }
    }
}
