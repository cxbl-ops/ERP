package com.erp.E02.service;

import ch.qos.logback.classic.Logger;
import com.erp.E02.model.Inventory;
import com.erp.E02.model.Product;
import com.erp.E02.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;



@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private Logger log;

    // 初始化库存（与产品联动）
    @Transactional
    public Inventory createInventory(Product product, int initialQuantity) {
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(initialQuantity);
        inventory.setLastUpdate(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    // 根据产品ID查询库存
    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product id: " + productId));
    }

    // 减少库存（原子操作）
    @Transactional
    public void decreaseQuantity(Long productId, int amount) {
        Inventory inventory = getInventoryByProductId(productId);
        if (inventory.getQuantity() < amount) {
            throw new RuntimeException("Insufficient stock for product id: " + productId);
        }
        inventory.setQuantity(inventory.getQuantity() - amount);
        inventory.setLastUpdate(LocalDateTime.now());
        inventoryRepository.save(inventory);
    }

    // 增加库存
    @Transactional
    public void increaseQuantity(Long productId, int amount) {
        Inventory inventory = getInventoryByProductId(productId);
        inventory.setQuantity(inventory.getQuantity() + amount);
        inventory.setLastUpdate(LocalDateTime.now());
        inventoryRepository.save(inventory);
    }

    // 获取低库存列表
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    // 更新低库存阈值
    @Transactional
    public void updateLowStockThreshold(Long productId, int threshold) {
        Inventory inventory = getInventoryByProductId(productId);
        inventory.setLowStockThreshold(threshold);
        inventoryRepository.save(inventory);
    }


    @Transactional
    public void deleteByProduct(Product product) {
        // 检查库存是否存在（避免误删）
        if (inventoryRepository.existsByProduct(product)) {
            inventoryRepository.deleteByProduct(product);
        }
         else {
             log.warn("Inventory not found for product: {}", product.getId());
         }
    }
}