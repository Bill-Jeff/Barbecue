package com.shaokao.controller;

import com.shaokao.entity.Category;
import com.shaokao.entity.Product;
import com.shaokao.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/categories")
    public List<Category> listCategories() {
        return productService.listCategories();
    }

    @GetMapping("/products")
    public Map<String, Object> listProducts(@RequestParam(required = false) Long categoryId) {
        List<Product> products = productService.listProducts(categoryId);
        Map<String, Object> result = new HashMap<>();
        result.put("list", products);
        return result;
    }
}
