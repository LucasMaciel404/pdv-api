package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.model.ProductModel;
import com.lucasmaciel404.pdv_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductModel> getAllProducts() {
        return productRepository.findAll();
    }

    public ProductModel createProduct(ProductModel productModel){
        return productRepository.save(productModel);
    }


    public void deleteProduct(UUID id) {
        ProductModel productModel = productRepository.findById(id).orElse(null);
        if  (productModel != null) {
            productRepository.delete(productModel);
        }
    }
}
