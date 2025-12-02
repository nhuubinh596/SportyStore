package com.example.backendquanaothethao.controller;

import com.example.backendquanaothethao.model.Category;
import com.example.backendquanaothethao.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    @Autowired
    private CategoryRepository repo;

    // 1. Lấy tất cả
    @GetMapping
    public List<Category> getAll() {
        return repo.findAll();
    }

    // 2. Thêm mới
    @PostMapping
    public Category create(@RequestBody Category c) {
        return repo.save(c);
    }

    // 3. Sửa tên danh mục (THÊM MỚI)
    @PutMapping("/{id}")
    public Category update(@PathVariable Long id, @RequestBody Category c) {
        return repo.findById(id)
                .map(cat -> {
                    cat.setName(c.getName());
                    return repo.save(cat);
                }).orElse(null);
    }

    // 4. Xóa
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}