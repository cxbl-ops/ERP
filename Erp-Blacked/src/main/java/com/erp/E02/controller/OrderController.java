package com.erp.E02.controller;

import com.erp.E02.config.StandardCreateApi;
import com.erp.E02.dto.OrderRequest;
import com.erp.E02.model.Order;
import com.erp.E02.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "订单管理", description = "订单管理接口")
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @StandardCreateApi(summary = "创建订单", description = "创建订单信息")
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }
    @StandardCreateApi(summary = "获取单个订单", description = "根据id获取单个订单")
    @GetMapping("/getone/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    @StandardCreateApi(summary = "获取所有订单", description = "查询所有订单")
    @GetMapping("/getAll")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    @StandardCreateApi(summary = "修改订单状态", description = "根据id修改订单状态")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }
}