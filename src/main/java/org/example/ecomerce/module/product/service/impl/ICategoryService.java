package org.example.ecomerce.module.product.service.impl;

import org.example.ecomerce.module.product.dto.CategoryDTO;
import org.example.ecomerce.module.product.entity.Category;

import java.util.List;

// ICategoryService.java
public interface ICategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO getCategoryById(Long id);

    List<CategoryDTO> getAllCategories();

    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);

    void deleteCategory(Long id);
}