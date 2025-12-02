package com.example.backendquanaothethao.repository;

import com.example.backendquanaothethao.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Tìm theo tên (không phân biệt hoa thường) + Phân trang
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}