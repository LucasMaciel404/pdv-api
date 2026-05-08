package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.model.ProductModel;
import com.lucasmaciel404.pdv_api.dto.request.ProductRequest;
import com.lucasmaciel404.pdv_api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productService;

    @GetMapping
    public List<ProductModel> getAllProducts(Authentication authentication) {
        try {
            return productService.getAllProducts( authentication.getName() );
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest request) {

        try {
            ProductModel product = new ProductModel();
            product.setName(request.name());
            product.setPrice(request.price());
            product.setDescription(request.description());
            product.setCategory(request.category());

            return ResponseEntity.ok(productService.createProduct(product, request.establishment()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/batch")
    public ResponseEntity<?> createProducts(@RequestBody List<ProductRequest> requests) {

        try {
            List<ProductModel> products = requests.stream().map(request -> {
                ProductModel product = new ProductModel();
                product.setName(request.name());
                product.setPrice(request.price());
                product.setDescription(request.description());
                product.setCategory(request.category());

                return productService.createProduct(product, request.establishment());
            }).toList();

            return ResponseEntity.ok(products);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
