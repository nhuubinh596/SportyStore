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
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public") // Đường dẫn công khai
@CrossOrigin(origins = "http://localhost:5173")
public class PublicController {

    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    // Lấy danh sách sản phẩm hiển thị ra trang chủ (Mới nhất lên đầu)
    @GetMapping("/products")
    public List<Product> getProducts() {
        return productRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productRepo.findById(id).orElse(null);
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest req) {
        try {
            // 1. Tạo đơn hàng
            Order order = new Order();
            order.setCreatedAt(LocalDateTime.now());
            order.setStatus("PENDING"); // Mặc định là Chờ xử lý

            // Gán User nếu có
            if (req.getUsername() != null) {
                UserAccount user = userRepository.findByUsername(req.getUsername()).orElse(null);
                order.setUser(user);
            }

            // 2. Xử lý chi tiết đơn hàng (Items)
            double total = 0;
            ArrayList<OrderItem> items = new ArrayList<>();

            for (OrderItemRequest itemReq : req.getItems()) {
                Product product = productRepo.findById(itemReq.getProductId()).orElse(null);
                if (product != null) {
                    OrderItem item = new OrderItem();
                    item.setProduct(product);
                    item.setOrder(order);
                    item.setQty(itemReq.getQuantity());
                    // Giá lấy từ request (đã tính giảm giá) hoặc lấy từ DB đều được
                    // Ở đây lấy từ DB cho an toàn hoặc request nếu tin tưởng FE
                    item.setPrice(BigDecimal.valueOf(itemReq.getPrice()));

                    items.add(item);
                    total += itemReq.getPrice() * itemReq.getQuantity();
                }
            }

            order.setItems(items);
            order.setTotalAmount(BigDecimal.valueOf(total));

            // 3. Lưu vào DB
            orderRepository.save(order);

            return ResponseEntity.ok(Map.of("message", "Đặt hàng thành công!", "orderId", order.getId()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi đặt hàng: " + e.getMessage()));
        }
    }
    // Tìm kiếm sản phẩm (Làm sau nếu cần)
}