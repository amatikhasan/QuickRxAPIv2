package com.example.quickrx.dto;

import com.example.quickrx.model.DiseaseImage;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DiseaseImageDto {
    private Long id;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DiseaseImageDto fromEntity(DiseaseImage entity) {
        if (entity == null) return null;
        DiseaseImageDto dto = new DiseaseImageDto();
        dto.setId(entity.getId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
