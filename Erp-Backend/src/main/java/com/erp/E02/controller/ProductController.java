package com.erp.E02.controller;


import com.erp.E02.config.StandardCreateApi;
import com.erp.E02.model.Product;
import com.erp.E02.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "产品管理", description = "产品信息管理接口")
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @StandardCreateApi(summary = "创建产品", description = "创建新产品并初始化库存")
    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product,
            @RequestParam(defaultValue = "0") int initialStock
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(product, initialStock));
    }
    @StandardCreateApi(summary = "获取所有产品", description = "获取所有产品")
    @GetMapping("/getAll")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    @StandardCreateApi(summary = "获取单个产品", description = "根据id获取产品")
    @GetMapping("/getone/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    @StandardCreateApi(summary = "更新产品", description = "根据id更新产品信息")
    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }
    @StandardCreateApi(summary = "删除产品", description = "根据id删除产品")
    @DeleteMapping("/del/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
