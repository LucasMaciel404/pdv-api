package com.lucasmaciel404.pdv_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @GetMapping("/")
    public String getAllProducts(){
        return "products";
    }

}
