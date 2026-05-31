package com.rizalamar.financetracker.service;

import com.rizalamar.financetracker.entity.Category;
import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.model.category.CategoryResponse;
import com.rizalamar.financetracker.model.category.CreateCategoryRequest;
import com.rizalamar.financetracker.model.category.UpdateCategoryRequest;
import com.rizalamar.financetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ValidationService validationService;
    private final UserDetailsManager userDetailsManager;


    public CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .categoryType(category.getCategoryType())
                .build();
    }

    @Transactional
    public CategoryResponse create(User user, CreateCategoryRequest request) {
        validationService.validate(request);

        Category category = new Category();
        category.setName(request.getName());
        category.setCategoryType(request.getCategoryType());
        category.setUser(user);

        categoryRepository.save(category);

        boolean isNameExists = categoryRepository.existsByUserAndName(user, request.getName());

        if(isNameExists){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name already exists");
        }

        return toCategoryResponse(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(User user, UUID id){
        Category category = categoryRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        return toCategoryResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(User user) {
        List<Category> categories = categoryRepository.findAllByUser(user);
        return categories.stream().map(this::toCategoryResponse).toList();
    }

    @Transactional
    public CategoryResponse update(User user, UUID id, UpdateCategoryRequest request){
        Category category = categoryRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if(Objects.nonNull(request.getName())){
            category.setName(request.getName());
        }

        if(Objects.nonNull(request.getCategoryType())){
            category.setCategoryType(request.getCategoryType());
        }

        categoryRepository.save(category);

        return toCategoryResponse(category);
    }

    @Transactional
    public void delete(User user, UUID id){
        Category category = categoryRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        categoryRepository.delete(category);
    }
}
