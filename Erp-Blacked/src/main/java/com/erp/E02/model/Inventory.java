package com.erp.E02.model;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Product product;

    private int quantity;
    private LocalDateTime lastUpdate;
    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold = 10; // 默认低库存阈值
}