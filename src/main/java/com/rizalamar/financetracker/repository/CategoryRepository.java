package com.rizalamar.financetracker.repository;

import com.rizalamar.financetracker.entity.Category;
import com.rizalamar.financetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findFirstByUserAndId(User user, UUID id);
    List<Category> findByUser(User user);
}
