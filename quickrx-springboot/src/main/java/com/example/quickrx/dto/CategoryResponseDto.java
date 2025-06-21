package com.example.quickrx.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.quickrx.model.Category; // Import Category model

@Data
public class CategoryResponseDto {
    private Long id;
    private String name;
    private Long parentCatId;
    private String parentCatName; // For convenience
    private Integer type;
    private String imageUrl;
    // Not exposing imageBytes directly in response unless necessary
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<CategoryResponseDto> subCategories; // For nested responses

    // Static factory method for conversion
    public static CategoryResponseDto fromEntity(Category category) {
        if (category == null) return null;

        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        if (category.getParentCategory() != null) {
            dto.setParentCatId(category.getParentCategory().getId());
            dto.setParentCatName(category.getParentCategory().getName());
        }
        dto.setType(category.getType());
        dto.setImageUrl(category.getImageUrl());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());

        // Avoid infinite recursion if subCategories also map their parent
        // This simple mapping is for one level deep. For deeper, consider @JsonManagedReference/@JsonBackReference
        // or more sophisticated mapping.
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            dto.setSubCategories(
                category.getSubCategories().stream()
                        .map(CategoryResponseDto::fromEntityWithoutSubCategories) // Use a method that doesn't map subcategories again
                        .collect(Collectors.toSet())
            );
        }
        return dto;
    }

    // Helper to prevent deep recursion for subcategories in this simple DTO mapping
    public static CategoryResponseDto fromEntityWithoutSubCategories(Category category) {
        if (category == null) return null;
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
         if (category.getParentCategory() != null) {
            dto.setParentCatId(category.getParentCategory().getId());
            dto.setParentCatName(category.getParentCategory().getName());
        }
        dto.setType(category.getType());
        dto.setImageUrl(category.getImageUrl());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        // No subCategories mapping here
        return dto;
    }
}
