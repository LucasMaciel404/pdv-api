package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.model.Establishment;
import com.lucasmaciel404.pdv_api.model.ProductModel;
import com.lucasmaciel404.pdv_api.model.UserEstablishment;
import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.EstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.ProductRepository;
import com.lucasmaciel404.pdv_api.repository.UserEstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final EstablishmentRepository establishmentRepository;
    private final UserEstablishmentRepository userEstablishmentRepository;
    private final UserRepository userRepository;

    public List<ProductModel> getAllProducts(String email) {
        UserModel user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        UserEstablishment userEstablishment = userEstablishmentRepository.findByUserId(user.getId()).orElseThrow(()->new RuntimeException("user establishment not found"));

        return productRepository.findByEstablishmentId(userEstablishment.getEstablishment().getId());
    }

    public ProductModel createProduct(ProductModel productModel, UUID establishment) {
        Establishment myEstablishment = establishmentRepository
                .findById(establishment)
                .orElseThrow(() -> new RuntimeException("Establishment not found"));

        productModel.setEstablishment(myEstablishment);

        return productRepository.save(productModel);
    }


    public void deleteProduct(UUID id) {
        ProductModel productModel = productRepository.findById(id).orElse(null);
        if  (productModel != null) {
            productRepository.delete(productModel);
        }
    }
}
