package com.example.quickrx.controller;

import com.example.quickrx.dto.CategoryRequestDto;
import com.example.quickrx.dto.CategoryResponseDto;
import com.example.quickrx.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> createCategory(
            @Valid @RequestPart("category") CategoryRequestDto categoryRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        // The @RequestPart("category") should map to a JSON part in multipart/form-data
        // The client needs to send 'category' as JSON and 'image' as a file.
        CategoryResponseDto createdCategory = categoryService.createCategory(categoryRequestDto, imageFile);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        CategoryResponseDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/subcategories/{parentId}")
    public ResponseEntity<List<CategoryResponseDto>> getSubCategoriesByParentId(@PathVariable Long parentId) {
        List<CategoryResponseDto> subCategories = categoryService.getAllSubCategoriesByParentId(parentId);
        return ResponseEntity.ok(subCategories);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestPart("category") CategoryRequestDto categoryRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        CategoryResponseDto updatedCategory = categoryService.updateCategory(id, categoryRequestDto, imageFile);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
