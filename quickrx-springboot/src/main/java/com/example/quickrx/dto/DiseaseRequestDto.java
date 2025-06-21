package com.example.quickrx.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiseaseRequestDto {

    @NotBlank(message = "Disease name cannot be blank")
    private String name;

    @NotNull(message = "Category ID cannot be null")
    private Long catId; // Foreign Key to Category

    // These fields correspond to 'disease' type entries
    private String clueToDx;
    private String advice;
    private String treatment;

    // This field corresponds to 'article' type entries in the old system
    private String details;

    // We can add a field to distinguish between "disease" and "article" if needed,
    // or infer based on which fields (clueToDx/advice/treatment vs details) are populated.
    // For simplicity, this DTO combines them. The service can decide how to map to the entity.
}
