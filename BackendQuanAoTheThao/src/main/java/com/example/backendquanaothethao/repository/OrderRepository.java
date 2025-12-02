package com.example.backendquanaothethao.repository;

import com.example.backendquanaothethao.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
