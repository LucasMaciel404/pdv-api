
package com.lucasmaciel404.pdv_api.dto.mapper;

import com.lucasmaciel404.pdv_api.dto.response.ProductResponse;
import com.lucasmaciel404.pdv_api.model.ProductModel;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(ProductModel product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getActive(),
                product.getCreatedAt(),
                product.getEstablishment().getId()
        );
    }
}
