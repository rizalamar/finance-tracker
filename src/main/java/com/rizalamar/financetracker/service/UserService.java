package com.rizalamar.financetracker.service;

import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.model.WebResponse;
import com.rizalamar.financetracker.model.user.UpdateUserResquest;
import com.rizalamar.financetracker.model.user.UserResponse;
import com.rizalamar.financetracker.repository.UserRepository;
import com.rizalamar.financetracker.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ValidationService validationService;

    public UserResponse toUserResponse(User user){
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserResponse update(User user, UpdateUserResquest request) {
        validationService.validate(request);

        if(Objects.nonNull(request.getName())) {
            user.setName(request.getName());
        }

        if(Objects.nonNull(request.getPassword())) {
            user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
        }

        userRepository.save(user);

        return toUserResponse(user);
    }
}
