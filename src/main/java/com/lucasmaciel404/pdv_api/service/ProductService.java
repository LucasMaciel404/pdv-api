
package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;
import com.lucasmaciel404.pdv_api.dto.request.ProductRequest;
import com.lucasmaciel404.pdv_api.model.Establishment;
import com.lucasmaciel404.pdv_api.model.ProductModel;
import com.lucasmaciel404.pdv_api.model.UserEstablishment;
import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.EstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.ProductRepository;
import com.lucasmaciel404.pdv_api.repository.UserEstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final EstablishmentRepository establishmentRepository;
    private final UserEstablishmentRepository userEstablishmentRepository;
    private final UserRepository userRepository;

    private UserEstablishment getUserEstablishment(String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"
                ));

        return userEstablishmentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Usuário não possui vínculo com um estabelecimento"
                ));
    }

    private UserEstablishment requireReadAccess(String email) {
        UserEstablishment link = getUserEstablishment(email);

        if (link.getRole() != UserRoleEnum.OWNER
                && link.getRole() != UserRoleEnum.WAITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para consultar produtos"
            );
        }

        return link;
    }

    private UserEstablishment requireOwner(String email, UUID establishmentId) {
        UserEstablishment link = getUserEstablishment(email);

        if (link.getRole() != UserRoleEnum.OWNER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Somente o proprietário pode alterar produtos"
            );
        }

        if (!link.getEstablishment().getId().equals(establishmentId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pode acessar outro estabelecimento"
            );
        }

        return link;
    }

    @Transactional(readOnly = true)
    public List<ProductModel> getAllProducts(String email) {
        UserEstablishment link = requireReadAccess(email);

        return productRepository.findByEstablishmentId(
                        link.getEstablishment().getId()
                )
                .stream()
                .filter(product -> Boolean.TRUE.equals(product.getActive()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductModel getProductById(UUID productId, String email) {
        UserEstablishment link = requireReadAccess(email);

        ProductModel product = productRepository
                .findByIdAndEstablishmentId(
                        productId,
                        link.getEstablishment().getId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado"
                ));

        return requireActiveProduct(product);
    }

    @Transactional
    public ProductModel createProduct(ProductRequest request, String email) {
        requireOwner(email, request.establishment());

        Establishment establishment = establishmentRepository
                .findById(request.establishment())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"
                ));

        ProductModel product = new ProductModel();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setDescription(request.description());
        product.setCategory(request.category());
        product.setEstablishment(establishment);

        return productRepository.save(product);
    }

    @Transactional
    public List<ProductModel> createProducts(
            List<ProductRequest> requests,
            String email
    ) {
        if (requests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "A lista de produtos está vazia"
            );
        }

        UUID establishmentId = requests.get(0).establishment();

        requireOwner(email, establishmentId);

        if (requests.stream().anyMatch(
                request -> !establishmentId.equals(request.establishment())
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Todos os produtos devem pertencer ao mesmo estabelecimento"
            );
        }

        Establishment establishment = establishmentRepository
                .findById(establishmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"
                ));

        List<ProductModel> products = requests.stream()
                .map(request -> {
                    ProductModel product = new ProductModel();
                    product.setName(request.name());
                    product.setPrice(request.price());
                    product.setDescription(request.description());
                    product.setCategory(request.category());
                    product.setEstablishment(establishment);
                    return product;
                })
                .toList();

        return productRepository.saveAll(products);
    }

    @Transactional
    public ProductModel updateProduct(
            UUID productId,
            ProductRequest request,
            String email
    ) {
        requireOwner(email, request.establishment());

        ProductModel product = productRepository
                .findByIdAndEstablishmentId(
                        productId, request.establishment()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado"
                ));

        requireActiveProduct(product);

        product.setName(request.name());
        product.setPrice(request.price());
        product.setDescription(request.description());
        product.setCategory(request.category());

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(UUID productId, String email) {
        UserEstablishment link = getUserEstablishment(email);

        if (link.getRole() != UserRoleEnum.OWNER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Somente o proprietário pode desativar produtos"
            );
        }

        ProductModel product = productRepository
                .findByIdAndEstablishmentId(
                        productId, link.getEstablishment().getId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado"
                ));

        requireActiveProduct(product);

        product.setActive(false);
        productRepository.save(product);
    }

    private ProductModel requireActiveProduct(ProductModel product) {
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Produto não encontrado ou inativo"
            );
        }

        return product;
    }
}