package com.rizalamar.financetracker.controller;

import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.model.WebResponse;
import com.rizalamar.financetracker.model.wallet.CreateWalletRequest;
import com.rizalamar.financetracker.model.wallet.UpdateWalletRequest;
import com.rizalamar.financetracker.model.wallet.WalletResponse;
import com.rizalamar.financetracker.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<WalletResponse> create(User user, @RequestBody CreateWalletRequest request) {
        WalletResponse walletResponse = walletService.create(user, request);
        return WebResponse.<WalletResponse>builder().data(walletResponse).build();
    }

    @GetMapping(path = "/{walletId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<WalletResponse> get(User user, @PathVariable("walletId") UUID id) {
        WalletResponse walletResponse = walletService.get(user, id);
        return WebResponse.<WalletResponse>builder().data(walletResponse).build();
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<WalletResponse>> list(User user) {
        List<WalletResponse> list = walletService.list(user);
        return WebResponse.<List<WalletResponse>>builder().data(list).build();
    }

    @PatchMapping(value = "/{walletId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<WalletResponse> update(User user, @PathVariable("walletId") UUID id, @RequestBody UpdateWalletRequest request) {
        WalletResponse walletResponse = walletService.update(user, id, request);
        return WebResponse.<WalletResponse>builder().data(walletResponse).build();
    }

    @DeleteMapping("/{walletId}")
    public WebResponse<String> delete(User user, @PathVariable("walletId") UUID id) {
        walletService.delete(user, id);
        return WebResponse.<String>builder().data("OK").build();
    }
}
