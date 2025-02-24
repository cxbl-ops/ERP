package com.erp.E02.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long productId; // 产品ID
    private int quantity;   // 购买数量
}