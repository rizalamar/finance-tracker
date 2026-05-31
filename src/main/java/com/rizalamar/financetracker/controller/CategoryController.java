package com.rizalamar.financetracker.controller;

import com.rizalamar.financetracker.entity.User;
import com.rizalamar.financetracker.model.WebResponse;
import com.rizalamar.financetracker.model.category.CategoryResponse;
import com.rizalamar.financetracker.model.category.CreateCategoryRequest;
import com.rizalamar.financetracker.model.category.UpdateCategoryRequest;
import com.rizalamar.financetracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<CategoryResponse> create (User user, @RequestBody CreateCategoryRequest request){
        CategoryResponse categoryResponse = categoryService.create(user, request);
        return WebResponse.<CategoryResponse>builder().data(categoryResponse).build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<CategoryResponse>> list(User user){
        List<CategoryResponse> list = categoryService.list(user);
        return WebResponse.<List<CategoryResponse>>builder().data(list).build();
    }

    @GetMapping(value = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<CategoryResponse> get(User user, @PathVariable("categoryId") UUID id){
        CategoryResponse categoryResponse = categoryService.get(user, id);
        return WebResponse.<CategoryResponse>builder().data(categoryResponse).build();
    }

    @PatchMapping(value = "/{categoryId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<CategoryResponse> update(User user, @PathVariable("categoryId") UUID id, @RequestBody UpdateCategoryRequest request){
        CategoryResponse updated = categoryService.update(user, id, request);
        return WebResponse.<CategoryResponse>builder().data(updated).build();
    }

    @DeleteMapping("/{categoryId}")
    public WebResponse<String> delete(User user, @PathVariable("categoryId") UUID id) {
        categoryService.delete(user, id);
        return  WebResponse.<String>builder().data("OK").build();
    }
}
