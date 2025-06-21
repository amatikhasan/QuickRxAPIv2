package com.example.quickrx.controller;

import com.example.quickrx.dto.DiseaseRequestDto;
import com.example.quickrx.dto.DiseaseResponseDto;
import com.example.quickrx.service.DiseaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiseaseResponseDto> createDisease(@Valid @RequestBody DiseaseRequestDto diseaseRequestDto) {
        DiseaseResponseDto createdDisease = diseaseService.createDisease(diseaseRequestDto);
        return new ResponseEntity<>(createdDisease, HttpStatus.CREATED);
    }

    @PostMapping("/{diseaseId}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiseaseResponseDto> addImageToDisease(
            @PathVariable Long diseaseId,
            @RequestParam("image") MultipartFile imageFile) {
        // Ensure client sends "image" as the parameter name for the file
        DiseaseResponseDto updatedDisease = diseaseService.addImageToDisease(diseaseId, imageFile);
        return ResponseEntity.ok(updatedDisease);
    }

    @GetMapping
    public ResponseEntity<List<DiseaseResponseDto>> getAllDiseases() {
        List<DiseaseResponseDto> diseases = diseaseService.getAllDiseases();
        return ResponseEntity.ok(diseases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiseaseResponseDto> getDiseaseById(@PathVariable Long id) {
        DiseaseResponseDto disease = diseaseService.getDiseaseById(id);
        return ResponseEntity.ok(disease);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<DiseaseResponseDto>> getDiseasesByCategoryId(@PathVariable Long categoryId) {
        List<DiseaseResponseDto> diseases = diseaseService.getDiseasesByCategoryId(categoryId);
        return ResponseEntity.ok(diseases);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiseaseResponseDto> updateDisease(
            @PathVariable Long id,
            @Valid @RequestBody DiseaseRequestDto diseaseRequestDto) {
        DiseaseResponseDto updatedDisease = diseaseService.updateDisease(id, diseaseRequestDto);
        return ResponseEntity.ok(updatedDisease);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDisease(@PathVariable Long id) {
        diseaseService.deleteDisease(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDiseaseImage(@PathVariable Long imageId) {
        diseaseService.deleteDiseaseImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
