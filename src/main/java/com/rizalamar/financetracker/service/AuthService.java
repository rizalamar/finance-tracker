package com.rizalamar.financetracker.service;

import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.model.RegisterUserRequest;
import com.rizalamar.financetracker.repository.UserRepository;
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

    @Transactional
    public void register(RegisterUserRequest request) {
        validationService.validate(request);

        if(userRepository.findFirstByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
        user.setName(request.getName());

        userRepository.save(user);
    }
}
