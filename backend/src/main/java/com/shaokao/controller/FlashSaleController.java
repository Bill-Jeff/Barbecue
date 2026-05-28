package com.shaokao.controller;

import com.shaokao.config.Result;
import com.shaokao.entity.FlashSale;
import com.shaokao.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FlashSaleController {

    private final FlashSaleService flashSaleService;

    @GetMapping("/api/flash-sales")
    public Result<List<FlashSale>> listActive() {
        return Result.success(flashSaleService.listActive());
    }

    @GetMapping("/api/admin/flash-sales")
    public Result<List<FlashSale>> listAll() {
        return Result.success(flashSaleService.listAll());
    }

    @PostMapping("/api/admin/flash-sales")
    public Result<Void> save(@RequestBody FlashSale flashSale) {
        flashSaleService.save(flashSale);
        return Result.success();
    }

    @DeleteMapping("/api/admin/flash-sales/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        flashSaleService.delete(id);
        return Result.success();
    }
}
