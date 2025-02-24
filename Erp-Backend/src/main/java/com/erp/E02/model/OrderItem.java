package com.erp.E02.model;

import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;

@Entity
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Order order;

    private int quantity;
    private BigDecimal unitPrice;
}