package com.shaokao.controller;

import com.shaokao.entity.FlashSale;
import com.shaokao.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FlashSaleController {

    private final FlashSaleService flashSaleService;

    @GetMapping("/api/flash-sales")
    public List<FlashSale> listActive() {
        return flashSaleService.listActive();
    }

    @GetMapping("/api/admin/flash-sales")
    public List<FlashSale> listAll() {
        return flashSaleService.listAll();
    }

    @PostMapping("/api/admin/flash-sales")
    public Map<String, Object> save(@RequestBody FlashSale flashSale) {
        flashSaleService.save(flashSale);
        Map<String, Object> result = new HashMap<>();
        result.put("id", flashSale.getId());
        result.put("message", "保存成功");
        return result;
    }

    @DeleteMapping("/api/admin/flash-sales/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        flashSaleService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }
}
