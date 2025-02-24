package com.erp.E02.service;

import com.erp.E02.dto.OrderItemRequest;
import com.erp.E02.dto.OrderRequest;
import com.erp.E02.model.Inventory;
import com.erp.E02.model.Order;
import com.erp.E02.model.OrderItem;
import com.erp.E02.model.Product;
import com.erp.E02.repository.InventoryRepository;
import com.erp.E02.repository.OrderRepository;
import com.erp.E02.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("CREATED");

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));

            if (inventory.getQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock");
            }

            inventory.setQuantity(inventory.getQuantity() - itemRequest.getQuantity());
            inventoryRepository.save(inventory);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setOrder(order);
            items.add(item);
        }

        order.setItems(items);
        return orderRepository.save(order);
    }
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    // 添加其他查询方法
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = getOrderById(orderId);
        order.setStatus(newStatus);
        orderRepository.save(order);
    }
}