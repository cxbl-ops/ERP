package com.erp.E02.service;

import com.erp.E02.model.Product;
import com.erp.E02.repository.InventoryRepository;
import com.erp.E02.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    // 创建产品（含库存初始化）
    @Transactional
    public Product createProduct(Product product, int initialStock) {
        Product savedProduct = productRepository.save(product);
        inventoryService.createInventory(savedProduct, initialStock); // 自动创建库存
        return savedProduct;
    }

    // 查询所有产品
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 根据ID查询产品
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    // 更新产品信息
    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        return productRepository.save(existing);
    }

    // 删除产品
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        inventoryService.deleteByProduct(product);
        productRepository.deleteById(id);
    }
}