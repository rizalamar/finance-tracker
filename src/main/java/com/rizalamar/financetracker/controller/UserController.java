package com.rizalamar.financetracker.controller;

import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.model.WebResponse;
import com.rizalamar.financetracker.model.user.UpdateUserResquest;
import com.rizalamar.financetracker.model.user.UserResponse;
import com.rizalamar.financetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UserResponse> get(User user) {
        UserResponse userResponse = userService.toUserResponse(user);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping(value = "/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserResquest request) {
        UserResponse updateUserResponse = userService.update(user, request);
        return WebResponse.<UserResponse>builder().data(updateUserResponse).build();
    }
}
