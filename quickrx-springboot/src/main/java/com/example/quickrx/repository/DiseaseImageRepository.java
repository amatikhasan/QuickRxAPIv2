package com.example.quickrx.repository;

import com.example.quickrx.model.DiseaseImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseImageRepository extends JpaRepository<DiseaseImage, Long> {
    List<DiseaseImage> findByDiseaseId(Long diseaseId);
}
