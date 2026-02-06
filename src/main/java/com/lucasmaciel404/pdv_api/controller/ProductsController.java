package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.model.ProductModel;
import com.lucasmaciel404.pdv_api.dto.request.ProductRequest;
import com.lucasmaciel404.pdv_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productService;

    @GetMapping
    public List<ProductModel> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    public ProductModel createProduct(@RequestBody ProductRequest request) {
        ProductModel product = new ProductModel();
        product.setName(request.name());
        product.setPrice(request.price());

        return productService.createProduct(product);
    }
}
