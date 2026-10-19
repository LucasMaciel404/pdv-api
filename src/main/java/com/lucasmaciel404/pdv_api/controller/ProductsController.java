
package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.mapper.ProductMapper;
import com.lucasmaciel404.pdv_api.dto.request.ProductRequest;
import com.lucasmaciel404.pdv_api.dto.response.ProductResponse;
import com.lucasmaciel404.pdv_api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public List<ProductResponse> getAllProducts(
            Authentication authentication
    ) {
        return productService.getAllProducts(authentication.getName())
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return productMapper.toResponse(
                productService.getProductById(id, authentication.getName())
        );
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request,
            Authentication authentication
    ) {
        ProductResponse response = productMapper.toResponse(
                productService.createProduct(request, authentication.getName())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ProductResponse>> createProducts(
            @RequestBody List<@Valid ProductRequest> requests,
            Authentication authentication
    ) {
        List<ProductResponse> responses = productService.createProducts(
                        requests, authentication.getName()
                )
                .stream()
                .map(productMapper::toResponse)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request,
            Authentication authentication
    ) {
        return productMapper.toResponse(
                productService.updateProduct(
                        id, request, authentication.getName()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        productService.deleteProduct(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
