package com.rizalamar.financetracker.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3-20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Usernames can only contain letters and numbers")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one number")
    private String password;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Maximum name is 100 characters")
    private String name;
}
