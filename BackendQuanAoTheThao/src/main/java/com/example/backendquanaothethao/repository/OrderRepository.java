package com.example.backendquanaothethao.repository;

import com.example.backendquanaothethao.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_UsernameOrderByCreatedAtAsc(String username);
}