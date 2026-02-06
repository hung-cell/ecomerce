package org.example.ecomerce.module.product.mapper;

import org.example.ecomerce.module.product.dto.ProductRequest;
import org.example.ecomerce.module.product.dto.ProductResponse;
import org.example.ecomerce.module.product.entity.Product;

public class ProductMapper {

    // Entity -> Response
    public static ProductResponse toResponse(Product product) {
        if (product == null) return null;

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .slug(product.getSlug())
                .createdAt(product.getCreatedAt())
                .build();
    }

    // Request -> Entity (CREATE)
    public static Product toEntity(ProductRequest request) {
        if (request == null) return null;

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());

        return product;
    }

    // UPDATE Entity from Request
    public static void updateEntity(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setPrice(request.getPrice());
    }
}
