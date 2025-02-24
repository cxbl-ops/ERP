package com.erp.E02.controller;

import com.erp.E02.config.StandardCreateApi;
import com.erp.E02.model.Inventory;
import com.erp.E02.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "库存管理", description = "库存管理接口")
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    @StandardCreateApi(summary = "查询产品库存", description = "根据productId查询产品库存")
    @GetMapping("/product/{productId}")
    public ResponseEntity<Inventory> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }
    @StandardCreateApi(summary = "减少库存", description = "根据productId减少单个产品库存")
    @PostMapping("/{productId}/decrease")
    public ResponseEntity<Void> decreaseStock(
            @PathVariable Long productId,
            @RequestParam int amount
    ) {
        inventoryService.decreaseQuantity(productId, amount);
        return ResponseEntity.ok().build();
    }
    @StandardCreateApi(summary = "增加库存", description = "根据productId增加单个产品库存")
    @PostMapping("/{productId}/increase")
    public ResponseEntity<Void> increaseStock(
            @PathVariable Long productId,
            @RequestParam int amount
    ) {
        inventoryService.increaseQuantity(productId, amount);
        return ResponseEntity.ok().build();
    }
    @StandardCreateApi(summary = "低库存", description = "查询低库存产品")
    @GetMapping("/low-stock")
    public ResponseEntity<List<Inventory>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }
    @StandardCreateApi(summary = "设置低库存阈值", description = "根据productId设置低库存阈值")
    @PatchMapping("/{productId}/threshold")
    public ResponseEntity<Void> updateThreshold(
            @PathVariable Long productId,
            @RequestParam int threshold
    ) {
        inventoryService.updateLowStockThreshold(productId, threshold);
        return ResponseEntity.ok().build();
    }
}