package com.example.quickrx.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequestDto {

    @NotBlank(message = "Category name cannot be blank")
    private String name;

    private Long parentCatId; // Nullable for main categories

    @NotNull(message = "Category type cannot be null")
    private Integer type; // e.g., 1 for Main Category, 2 for Sub Category

    // For file uploads, we'll handle them separately in the controller.
    // This DTO is for metadata.
    // image_url might be set by the service after file upload.
    // image_bytes could be an alternative if we pass bytes directly.
}
