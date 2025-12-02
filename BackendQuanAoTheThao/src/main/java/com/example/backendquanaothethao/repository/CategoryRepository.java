package com.example.backendquanaothethao.repository;

import com.example.backendquanaothethao.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
