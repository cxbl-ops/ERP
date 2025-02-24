package com.erp.E02.repository;

import com.erp.E02.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name); // 根据产品名称查询
}