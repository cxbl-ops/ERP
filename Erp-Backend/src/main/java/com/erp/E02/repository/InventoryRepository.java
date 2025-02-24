package com.erp.E02.repository;

import com.erp.E02.model.Inventory;
import com.erp.E02.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // 根据产品ID查询库存
    Optional<Inventory> findByProductId(Long productId);

    // 查询低库存记录（自定义JPQL）
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.lowStockThreshold")
    List<Inventory> findLowStockItems();
    // 根据产品删除库存记录
    void deleteByProduct(Product product); // 方法名衍生查询

    boolean existsByProduct(Product product);
}