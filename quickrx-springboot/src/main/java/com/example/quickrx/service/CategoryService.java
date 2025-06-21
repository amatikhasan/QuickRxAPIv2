package com.example.quickrx.service;

import com.example.quickrx.dto.CategoryRequestDto;
import com.example.quickrx.dto.CategoryResponseDto;
import com.example.quickrx.model.Category;
import com.example.quickrx.repository.CategoryRepository;
import com.example.quickrx.exception.ResourceNotFoundException;
import com.example.quickrx.exception.InvalidRequestException; // For validation like preventing deletion
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private static final String CATEGORY_IMAGE_SUBDIR = "category_images";

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto, MultipartFile imageFile) {
        Category category = new Category();
        category.setName(categoryRequestDto.getName());
        category.setType(categoryRequestDto.getType());

        if (categoryRequestDto.getParentCatId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequestDto.getParentCatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + categoryRequestDto.getParentCatId()));
            category.setParentCategory(parentCategory);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile, CATEGORY_IMAGE_SUBDIR);
            // Store relative path or full URL based on how you serve files
            // For now, storing relative path: "category_images/filename.jpg"
            category.setImageUrl(fileName);
            category.setImageBytes(null); // Clear imageBytes if imageUrl is set
        } else if (categoryRequestDto.getType() == 1 && category.getImageUrl() == null && category.getImageBytes() == null) {
             // Example: Main categories might require an image (or image_bytes previously)
             // This logic depends on your specific requirements from the old PHP code.
             // The old code had separate createCategoryWithFile and createCategory (with image_bytes).
             // We're unifying this. If image is mandatory, add validation here or on DTO.
        }


        Category savedCategory = categoryRepository.save(category);
        return CategoryResponseDto.fromEntity(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        // Fetch only top-level categories (those without a parent)
        // Type 1 was main category in PHP
        return categoryRepository.findByParentCategoryIsNullAndType(1)
                .stream()
                .map(CategoryResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllSubCategoriesByParentId(Long parentId) {
        if (!categoryRepository.existsById(parentId)) {
            throw new ResourceNotFoundException("Parent category not found with id: " + parentId);
        }
        return categoryRepository.findByParentCategoryId(parentId)
                .stream()
                .map(CategoryResponseDto::fromEntity)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return CategoryResponseDto.fromEntity(category);
    }

    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequestDto, MultipartFile imageFile) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setName(categoryRequestDto.getName());
        category.setType(categoryRequestDto.getType());

        if (categoryRequestDto.getParentCatId() != null) {
            if (categoryRequestDto.getParentCatId().equals(id)) {
                throw new InvalidRequestException("Category cannot be its own parent.");
            }
            Category parentCategory = categoryRepository.findById(categoryRequestDto.getParentCatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + categoryRequestDto.getParentCatId()));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            // Delete old file if it exists
            if (category.getImageUrl() != null && !category.getImageUrl().isBlank()) {
                fileStorageService.deleteFile(category.getImageUrl());
            }
            String fileName = fileStorageService.storeFile(imageFile, CATEGORY_IMAGE_SUBDIR);
            category.setImageUrl(fileName);
            category.setImageBytes(null); // Clear imageBytes if imageUrl is set
        }

        Category updatedCategory = categoryRepository.save(category);
        return CategoryResponseDto.fromEntity(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Handle subcategories: The Category entity has CascadeType.ALL for subCategories,
        // which means they'd be deleted automatically by Hibernate if they exist.
        // If this is not desired, the CascadeType should be changed or subcategories handled manually.
        // For example, to prevent deletion if subcategories exist:
        if (!category.getSubCategories().isEmpty()) {
             throw new InvalidRequestException("Cannot delete category with subcategories. Please delete or reassign subcategories first.");
        }

        // Delete associated image file
        if (category.getImageUrl() != null && !category.getImageUrl().isBlank()) {
            fileStorageService.deleteFile(category.getImageUrl());
        }

        categoryRepository.delete(category);
    }
}
