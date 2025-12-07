package com.example.backendquanaothethao.controller;

import com.example.backendquanaothethao.dto.OrderItemRequest;
import com.example.backendquanaothethao.dto.OrderRequest;
import com.example.backendquanaothethao.model.Order;
import com.example.backendquanaothethao.model.OrderItem;
import com.example.backendquanaothethao.model.Product;
import com.example.backendquanaothethao.model.UserAccount;
import com.example.backendquanaothethao.repository.OrderRepository;
import com.example.backendquanaothethao.repository.ProductRepository;
import com.example.backendquanaothethao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:5173")
public class PublicController {

    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/products")
    public Page<Product> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        Sort sorting = Sort.by(Sort.Direction.DESC, "id");
        if ("price_asc".equals(sort)) {
            sorting = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price_desc".equals(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "price");
        }
        Pageable pageable = PageRequest.of(page, size, sorting);
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepo.findAll(pageable);
        } else {
            return productRepo.findByNameContainingIgnoreCase(keyword, pageable);
        }
    }

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productRepo.findById(id).orElse(null);
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest req) {
        try {
            Order order = new Order();
            order.setCreatedAt(LocalDateTime.now());
            order.setStatus("PENDING");
            order.setFullName(req.getFullName());
            order.setPhone(req.getPhone());
            order.setAddress(req.getAddress());
            order.setPaymentMethod("COD");
            if (req.getUsername() != null) {
                UserAccount user = userRepository.findByUsername(req.getUsername()).orElse(null);
                order.setUser(user);
            }
            double total = 0;
            ArrayList<OrderItem> items = new ArrayList<>();

            for (OrderItemRequest itemReq : req.getItems()) {
                Product product = productRepo.findById(itemReq.getProductId()).orElse(null);
                if (product != null) {
                    OrderItem item = new OrderItem();
                    item.setProduct(product);
                    item.setOrder(order);
                    item.setQty(itemReq.getQuantity());
                    item.setPrice(BigDecimal.valueOf(itemReq.getPrice()));
                    items.add(item);
                    total += itemReq.getPrice() * itemReq.getQuantity();
                }
            }
            order.setItems(items);
            order.setTotalAmount(BigDecimal.valueOf(total));
            orderRepository.save(order);

            return ResponseEntity.ok(Map.of("message", "Đặt hàng thành công!", "orderId", order.getId()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi đặt hàng: " + e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public List<Order> getMyOrders(@RequestParam("username") String username) {
        return orderRepository.findByUser_UsernameOrderByCreatedAtAsc(username);
    }
}