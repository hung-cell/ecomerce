package org.example.ecomerce.module.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ecomerce.module.product.dto.CategoryDTO;
import org.example.ecomerce.module.product.entity.Category;
import org.example.ecomerce.module.product.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories") // Đường dẫn chuẩn
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 1. Tạo danh mục mới (POST)
    @PostMapping("")
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        CategoryDTO category = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(category);
    }

    // 2. Lấy tất cả danh mục (GET)
    @GetMapping("")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // 3. Sửa danh mục (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    // 4. Xóa danh mục (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Delete category successfully");
    }
}