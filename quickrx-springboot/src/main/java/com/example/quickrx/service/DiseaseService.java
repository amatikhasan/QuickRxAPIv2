package com.example.quickrx.service;

import com.example.quickrx.dto.DiseaseRequestDto;
import com.example.quickrx.dto.DiseaseResponseDto;
import com.example.quickrx.model.Category;
import com.example.quickrx.model.Disease;
import com.example.quickrx.model.DiseaseImage;
import com.example.quickrx.repository.CategoryRepository;
import com.example.quickrx.repository.DiseaseRepository;
import com.example.quickrx.repository.DiseaseImageRepository;
import com.example.quickrx.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiseaseService {

    private static final String DISEASE_IMAGE_SUBDIR_PREFIX = "disease_images";

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DiseaseImageRepository diseaseImageRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public DiseaseResponseDto createDisease(DiseaseRequestDto diseaseRequestDto) {
        Category category = categoryRepository.findById(diseaseRequestDto.getCatId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + diseaseRequestDto.getCatId()));

        Disease disease = new Disease();
        disease.setName(diseaseRequestDto.getName());
        disease.setCategory(category);
        disease.setClueToDx(diseaseRequestDto.getClueToDx());
        disease.setAdvice(diseaseRequestDto.getAdvice());
        disease.setTreatment(diseaseRequestDto.getTreatment());
        disease.setDetails(diseaseRequestDto.getDetails()); // For articles

        Disease savedDisease = diseaseRepository.save(disease);
        return DiseaseResponseDto.fromEntity(savedDisease);
    }

    @Transactional
    public DiseaseResponseDto addImageToDisease(Long diseaseId, MultipartFile imageFile) {
        Disease disease = diseaseRepository.findById(diseaseId)
            .orElseThrow(() -> new ResourceNotFoundException("Disease not found with id: " + diseaseId));

        String subDirectory = DISEASE_IMAGE_SUBDIR_PREFIX + "/" + diseaseId; // Store images in disease-specific subfolders
        String fileName = fileStorageService.storeFile(imageFile, subDirectory);

        DiseaseImage diseaseImage = new DiseaseImage();
        diseaseImage.setDisease(disease);
        diseaseImage.setImageUrl(fileName); // This will be "disease_images/diseaseId/uuid.jpg"
        diseaseImageRepository.save(diseaseImage);

        // Refresh disease entity to include the new image by re-fetching
        Disease updatedDisease = diseaseRepository.findById(diseaseId)
            .orElseThrow(() -> new ResourceNotFoundException("Disease not found after image add with id: " + diseaseId)); // Should not happen
        return DiseaseResponseDto.fromEntity(updatedDisease);
    }

    @Transactional(readOnly = true)
    public List<DiseaseResponseDto> getAllDiseases() {
        return diseaseRepository.findAll().stream()
                .map(DiseaseResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DiseaseResponseDto getDiseaseById(Long id) {
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found with id: " + id));
        return DiseaseResponseDto.fromEntity(disease);
    }

    @Transactional(readOnly = true)
    public List<DiseaseResponseDto> getDiseasesByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        return diseaseRepository.findByCategoryId(categoryId).stream()
                .map(DiseaseResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public DiseaseResponseDto updateDisease(Long id, DiseaseRequestDto diseaseRequestDto) {
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found with id: " + id));

        Category category = categoryRepository.findById(diseaseRequestDto.getCatId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + diseaseRequestDto.getCatId()));

        disease.setName(diseaseRequestDto.getName());
        disease.setCategory(category);
        disease.setClueToDx(diseaseRequestDto.getClueToDx());
        disease.setAdvice(diseaseRequestDto.getAdvice());
        disease.setTreatment(diseaseRequestDto.getTreatment());
        disease.setDetails(diseaseRequestDto.getDetails());

        Disease updatedDisease = diseaseRepository.save(disease);
        return DiseaseResponseDto.fromEntity(updatedDisease);
    }

    @Transactional
    public void deleteDisease(Long id) {
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found with id: " + id));

        // Delete associated images from FileStorageService
        // DiseaseImages are deleted by cascade due to orphanRemoval=true in Disease entity's OneToMany mapping.
        // So, we just need to delete the files.
        if (disease.getDiseaseImages() != null) {
            for (DiseaseImage image : disease.getDiseaseImages()) {
                if (image.getImageUrl() != null && !image.getImageUrl().isBlank()) {
                    fileStorageService.deleteFile(image.getImageUrl());
                }
            }
        }
        diseaseRepository.delete(disease);
    }

    @Transactional
    public void deleteDiseaseImage(Long diseaseImageId) {
        DiseaseImage diseaseImage = diseaseImageRepository.findById(diseaseImageId)
            .orElseThrow(() -> new ResourceNotFoundException("Disease image not found with id: " + diseaseImageId));

        if (diseaseImage.getImageUrl() != null && !diseaseImage.getImageUrl().isBlank()) {
            fileStorageService.deleteFile(diseaseImage.getImageUrl());
        }

        diseaseImageRepository.delete(diseaseImage);
    }
}
