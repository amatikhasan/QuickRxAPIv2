package com.example.quickrx.dto;

import com.example.quickrx.model.Disease;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class DiseaseResponseDto {
    private Long id;
    private String name;
    private Long catId;
    private String categoryName; // For convenience
    private String clueToDx;
    private String advice;
    private String treatment;
    private String details; // For articles
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<DiseaseImageDto> diseaseImages;

    public static DiseaseResponseDto fromEntity(Disease disease) {
        if (disease == null) return null;

        DiseaseResponseDto dto = new DiseaseResponseDto();
        dto.setId(disease.getId());
        dto.setName(disease.getName());
        if (disease.getCategory() != null) {
            dto.setCatId(disease.getCategory().getId());
            dto.setCategoryName(disease.getCategory().getName());
        }
        dto.setClueToDx(disease.getClueToDx());
        dto.setAdvice(disease.getAdvice());
        dto.setTreatment(disease.getTreatment());
        dto.setDetails(disease.getDetails());
        dto.setCreatedAt(disease.getCreatedAt());
        dto.setUpdatedAt(disease.getUpdatedAt());

        if (disease.getDiseaseImages() != null) {
            dto.setDiseaseImages(
                disease.getDiseaseImages().stream()
                        .map(DiseaseImageDto::fromEntity)
                        .collect(Collectors.toSet())
            );
        }
        return dto;
    }
}
