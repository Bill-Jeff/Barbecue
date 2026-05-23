package com.shaokao.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shaokao.entity.FlashSale;
import com.shaokao.entity.Product;
import com.shaokao.mapper.FlashSaleMapper;
import com.shaokao.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlashSaleService {

    private final FlashSaleMapper flashSaleMapper;
    private final ProductMapper productMapper;

    public List<FlashSale> listActive() {
        List<FlashSale> sales = flashSaleMapper.selectList(
                new LambdaQueryWrapper<FlashSale>()
                        .gt(FlashSale::getSaleTime, LocalDateTime.now())
                        .gt(FlashSale::getCount, 0)
                        .orderByAsc(FlashSale::getSaleTime));
        // 关联 product 信息
        for (FlashSale sale : sales) {
            Product product = productMapper.selectById(sale.getProductId());
            if (product != null) {
                sale.setProductName(product.getName());
                sale.setProductImage(product.getImage());
                sale.setOriginalPrice(product.getPrice());
            }
        }
        return sales;
    }

    public List<FlashSale> listAll() {
        List<FlashSale> sales = flashSaleMapper.selectList(
                new LambdaQueryWrapper<FlashSale>().orderByAsc(FlashSale::getSaleTime));
        for (FlashSale sale : sales) {
            Product product = productMapper.selectById(sale.getProductId());
            if (product != null) {
                sale.setProductName(product.getName());
                sale.setProductImage(product.getImage());
                sale.setOriginalPrice(product.getPrice());
            }
        }
        return sales;
    }

    public void save(FlashSale flashSale) {
        if (flashSale.getId() == null) {
            flashSaleMapper.insert(flashSale);
        } else {
            flashSaleMapper.updateById(flashSale);
        }
    }

    public void delete(Long id) {
        flashSaleMapper.deleteById(id);
    }
}
