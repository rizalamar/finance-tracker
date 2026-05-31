package com.rizalamar.financetracker.repository;

import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findFirstByUserAndId(User user, UUID id);
}
