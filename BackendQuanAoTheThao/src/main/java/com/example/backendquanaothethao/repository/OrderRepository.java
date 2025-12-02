package com.example.backendquanaothethao.repository;

import com.example.backendquanaothethao.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Tìm đơn hàng theo Username, sắp xếp mới nhất lên đầu
    List<Order> findByUser_UsernameOrderByCreatedAtAsc(String username);
}