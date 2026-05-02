package com.shaokao.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shaokao.entity.Category;
import com.shaokao.entity.Product;
import com.shaokao.mapper.CategoryMapper;
import com.shaokao.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    public List<Category> listCategories() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
    }

    public void saveCategory(Category category) {
        if (category.getId() == null) {
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateById(category);
        }
    }

    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    public List<Product> listProducts(Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .orderByAsc(Product::getSortOrder);
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        return productMapper.selectList(wrapper);
    }

    public List<Product> listAllProducts() {
        return productMapper.selectList(
                new LambdaQueryWrapper<Product>().orderByAsc(Product::getCategoryId)
                        .orderByAsc(Product::getSortOrder));
    }

    public void saveProduct(Product product) {
        if (product.getId() == null) {
            productMapper.insert(product);
        } else {
            productMapper.updateById(product);
        }
    }

    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }
}
