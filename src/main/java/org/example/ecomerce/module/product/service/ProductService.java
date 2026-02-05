package org.example.ecomerce.module.product.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecomerce.module.product.dto.ProductRequest;
import org.example.ecomerce.module.product.dto.ProductResponse;
import org.example.ecomerce.module.product.dto.ProductSearchRequest;
import org.example.ecomerce.module.product.entity.Category;
import org.example.ecomerce.module.product.entity.Product;
import org.example.ecomerce.module.product.enums.ProductStatus;
import org.example.ecomerce.module.product.repository.CategoryRepository;
import org.example.ecomerce.module.product.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository; // Inject Repository để tìm Category
    private final ModelMapper modelMapper;

    // ==================== READ OPERATIONS (ĐỌC DỮ LIỆU) ====================

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = findProductByIdOrThrow(id);
        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with slug: " + slug));

        // Tăng view count mỗi khi có người xem chi tiết
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        return mapToResponse(product);
    }

    // --- SEARCH & FILTER (TÌM KIẾM NÂNG CAO) ---
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(ProductSearchRequest request, Pageable pageable) {
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Tìm theo tên hoặc mô tả
            if (StringUtils.hasText(request.getKeyword())) {
                String likePattern = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern)
                ));
            }

            // 2. Lọc theo Category (QUAN TRỌNG: Cập nhật theo Entity mới)
            if (request.getCategoryId() != null) {
                // Vì quan hệ là Object, ta phải .get("category").get("id")
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), request.getCategoryId()));
            }

            // 3. Các bộ lọc khác (Brand, Status, Featured...)
            if (request.getBrandId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("brandId"), request.getBrandId()));
            }
            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            if (Boolean.TRUE.equals(request.getFeatured())) {
                predicates.add(criteriaBuilder.equal(root.get("featured"), true));
            }

            // 4. Lọc theo khoảng giá
            if (request.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    // ==================== WRITE OPERATIONS (GHI DỮ LIỆU) ====================

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        // 1. Validate: Tên và SKU không được trùng
        if (productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Product name already exists");
        }
        if (StringUtils.hasText(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("SKU already exists");
        }

        // 2. TÌM CATEGORY (Bước quan trọng nhất của quan hệ ManyToOne)
        // Phải đảm bảo Category tồn tại trước khi gán vào Product
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + request.getCategoryId()));

        // 3. Map DTO -> Entity
        Product product = modelMapper.map(request, Product.class);

        // 4. Gán Category vào Product
        product.setCategory(category);

        // 5. Xử lý logic phụ (Slug, Status...)
        product.setSlug(generateSlug(request.getName()));
        updateStatusBasedOnStock(product);

        // 6. Lưu xuống DB
        return mapToResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProductByIdOrThrow(id);

        // Check trùng tên (nếu đổi tên mới)
        if (!product.getName().equals(request.getName()) && productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Product name already exists");
        }

        // 1. Xử lý Update Category (Nếu user chọn danh mục khác)
        // So sánh ID danh mục cũ và mới
        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(newCategory);
        }

        // 2. Map dữ liệu mới vào
        modelMapper.map(request, product);

        // 3. Update Slug nếu tên thay đổi
        if (!product.getName().equals(request.getName())) {
            product.setSlug(generateSlug(request.getName()));
        }
        updateStatusBasedOnStock(product);

        return mapToResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // --- UPDATE STOCK (Hàm này trước bị thiếu) ---
    @Transactional
    public ProductResponse updateStock(Long id, Integer quantity) {
        // 1. Kiểm tra số lượng hợp lệ (không âm)
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        // 2. Tìm sản phẩm
        Product product = findProductByIdOrThrow(id);

        // 3. Cập nhật số lượng
        product.setStock(quantity);

        // 4. Logic tự động: Hết hàng thì set OUT_OF_STOCK, có hàng thì set ACTIVE
        updateStatusBasedOnStock(product);

        // 5. Lưu và trả về
        return mapToResponse(productRepository.save(product));
    }

    // ==================== IMAGE UPLOAD LOGIC (MỚI) ====================

    @Transactional
    public ProductResponse uploadImage(Long productId, MultipartFile file) throws IOException {
        // 1. Tìm sản phẩm
        Product product = findProductByIdOrThrow(productId);

        // 2. Validate File (Rỗng? Quá lớn? Không phải ảnh?)
        if (file.isEmpty()) throw new IllegalArgumentException("File cannot be empty");
        if (file.getSize() > 10 * 1024 * 1024) throw new IllegalArgumentException("File size > 10MB");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // 3. Lưu file vào ổ cứng & Lấy tên file mới
        String filename = storeFile(file);

        // 4. Update đường dẫn vào DB (Cột thumbnail)
        product.setThumbnail(filename);

        return mapToResponse(productRepository.save(product));
    }

    // Hàm private hỗ trợ lưu file
    private String storeFile(MultipartFile file) throws IOException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        // Tạo tên file ngẫu nhiên để không bị trùng (UUID)
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        Path uploadDir = Paths.get("uploads");
        // Tạo thư mục nếu chưa có
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // Copy file
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }

    // ==================== HELPER METHODS ====================

    private Product findProductByIdOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    private void updateStatusBasedOnStock(Product product) {
        if (product.getStock() == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else if (product.getStatus() == ProductStatus.OUT_OF_STOCK) {
            product.setStatus(ProductStatus.ACTIVE);
        }
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }

    // Map Entity -> Response DTO
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = modelMapper.map(product, ProductResponse.class);

        // Map thủ công Category ID để trả về cho Frontend
        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            // response.setCategoryName(product.getCategory().getName()); // Nếu muốn trả thêm tên
        }
        return response;
    }
    // ==================== STATISTICS OPERATIONS ====================

    @Transactional(readOnly = true)
    public long countAllProducts() {
        return productRepository.count(); // Tự động loại bỏ sản phẩm đã xóa mềm
    }

    @Transactional(readOnly = true)
    public long countOutOfStock() {
        return productRepository.countByStock(0);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalInventoryValue() {
        return productRepository.getTotalInventoryValue();
    }
}