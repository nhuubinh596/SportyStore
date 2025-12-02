package com.example.backendquanaothethao.repository;

import com.example.backendquanaothethao.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}
