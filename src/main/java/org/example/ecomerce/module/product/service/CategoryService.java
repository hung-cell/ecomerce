package org.example.ecomerce.module.product.service;

import lombok.RequiredArgsConstructor;
import org.example.ecomerce.module.product.dto.CategoryDTO;
import org.example.ecomerce.module.product.entity.Category;
import org.example.ecomerce.module.product.repository.CategoryRepository;
import org.example.ecomerce.module.product.service.impl.ICategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Tự động Inject Repository (Thay cho @Autowired)
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO) {
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

        // 3. Save
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());
        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        // Mẹo Senior: Sau này nên check xem Category có chứa Product không trước khi xóa
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }
        categoryRepository.deleteById(id);
    }
}