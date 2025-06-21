package com.example.quickrx.repository;

import com.example.quickrx.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(Integer type);
    List<Category> findByParentCategoryIsNullAndType(Integer type); // For main categories
    List<Category> findByParentCategoryId(Long parentCatId);
}
