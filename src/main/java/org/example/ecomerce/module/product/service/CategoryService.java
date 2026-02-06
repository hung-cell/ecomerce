package org.example.ecomerce.module.product.service;

import lombok.RequiredArgsConstructor;
import org.example.ecomerce.module.product.dto.CategoryDTO;
import org.example.ecomerce.module.product.entity.Category;
import org.example.ecomerce.module.product.repository.CategoryRepository;
import org.example.ecomerce.module.product.repository.ProductRepository;
import org.example.ecomerce.module.product.service.impl.ICategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Tự động Inject Repository (Thay cho @Autowired)
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository; // Injected ProductRepository

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // 1. Validate: Check trùng tên
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new RuntimeException("Category " + categoryDTO.getName() + " already exists");
        }

        // 2. Convert DTO -> Entity
        Category newCategory = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                // .slug(generateSlug(categoryDTO.getName())) // Nếu bạn muốn làm slug tự động
                .build();

        // 3. Save & Convert to DTO
        Category savedCategory = categoryRepository.save(newCategory);
        return toDTO(savedCategory);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return toDTO(category);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return toDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }

        // Safe Delete: Check if products exist in this category
        if (productRepository.existsByCategoryId(id)) {
            throw new RuntimeException(
                    "Cannot delete category because it contains products. Please delete products first.");
        }

        categoryRepository.deleteById(id);
    }

    // Helper method to convert Entity -> DTO
    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}