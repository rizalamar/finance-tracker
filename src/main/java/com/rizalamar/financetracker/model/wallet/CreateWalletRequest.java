package com.rizalamar.financetracker.model.wallet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateWalletRequest {
    @NotBlank(message = "Name of wallet is required.")
    @Size(max = 100)
    private String name;

    @NotNull
    private Long balance;
}
