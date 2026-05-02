package com.shaokao.controller;

import com.shaokao.entity.Category;
import com.shaokao.entity.Product;
import com.shaokao.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    // ========== 分类管理 ==========

    @GetMapping("/categories")
    public List<Category> listCategories() {
        return productService.listCategories();
    }

    @PostMapping("/categories")
    public Map<String, Object> saveCategory(@RequestBody Category category) {
        productService.saveCategory(category);
        Map<String, Object> result = new HashMap<>();
        result.put("id", category.getId());
        result.put("message", "保存成功");
        return result;
    }

    @DeleteMapping("/categories/{id}")
    public Map<String, Object> deleteCategory(@PathVariable Long id) {
        productService.deleteCategory(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }

    // ========== 菜品管理 ==========

    @GetMapping("/products")
    public List<Product> listAllProducts() {
        return productService.listAllProducts();
    }

    @PostMapping("/products")
    public Map<String, Object> saveProduct(@RequestBody Product product) {
        productService.saveProduct(product);
        Map<String, Object> result = new HashMap<>();
        result.put("id", product.getId());
        result.put("message", "保存成功");
        return result;
    }

    @DeleteMapping("/products/{id}")
    public Map<String, Object> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }

    // ========== 图片上传 ==========

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) throws IOException {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        String ext = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID() + ext;
        Path path = Paths.get(UPLOAD_DIR, fileName);
        Files.write(path, file.getBytes());

        Map<String, Object> result = new HashMap<>();
        result.put("url", "/uploads/" + fileName);
        return result;
    }
}
