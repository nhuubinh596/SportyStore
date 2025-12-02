package com.example.backendquanaothethao.controller;

import com.example.backendquanaothethao.model.Order;
import com.example.backendquanaothethao.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderRepository repo;
    private final OrderRepository orderRepository;

    public OrderController(OrderRepository repo, OrderRepository orderRepository) {
        this.repo = repo;
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<Order> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    // 3. Cập nhật trạng thái đơn hàng
    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            String newStatus = body.get("status");
            order.setStatus(newStatus);
            return orderRepository.save(order);
        }
        return null;
    }
}
