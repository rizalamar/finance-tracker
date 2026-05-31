package com.rizalamar.financetracker.service;


import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.entity.Wallet;
import com.rizalamar.financetracker.model.wallet.CreateWalletRequest;
import com.rizalamar.financetracker.model.wallet.UpdateWalletRequest;
import com.rizalamar.financetracker.model.wallet.WalletResponse;
import com.rizalamar.financetracker.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final ValidationService validationService;

    public WalletResponse toWalletResponse(Wallet wallet){
        return WalletResponse.builder()
                .id(wallet.getId())
                .name(wallet.getName())
                .balance(wallet.getBalance())
                .build();
    }

    @Transactional
    public WalletResponse create (User user, CreateWalletRequest request) {
        validationService.validate(request);

        Wallet wallet = new Wallet();
        wallet.setName(request.getName());
        wallet.setBalance(request.getBalance());
        wallet.setUser(user);
        walletRepository.save(wallet);

        return toWalletResponse(wallet);
    }

    @Transactional
    public WalletResponse get (User user, UUID id) {
        Wallet wallet = walletRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));

        return toWalletResponse(wallet);
    }

    @Transactional(readOnly = true)
    public List<WalletResponse> list(User user) {
        List<Wallet> wallets = walletRepository.findAllByUser(user);
        return wallets.stream().map(this::toWalletResponse).toList();
    }

    @Transactional
    public WalletResponse update(User user, UUID id, UpdateWalletRequest request){
        validationService.validate(request);

        Wallet wallet = walletRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));

        if(Objects.nonNull(request.getName())){
            wallet.setName(request.getName());
        }

        walletRepository.save(wallet);

        return  toWalletResponse(wallet);
    }

    @Transactional
    public void delete(User user, UUID id){
        Wallet wallet = walletRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));
        walletRepository.delete(wallet);
    }
}
