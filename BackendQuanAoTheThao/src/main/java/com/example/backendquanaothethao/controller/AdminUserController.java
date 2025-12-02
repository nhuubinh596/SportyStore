package com.example.backendquanaothethao.controller;

import com.example.backendquanaothethao.model.UserAccount;
import com.example.backendquanaothethao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminUserController {

    @Autowired
    private UserRepository userRepo;

    // 1. Lấy danh sách user (Sắp xếp mới nhất lên đầu nếu muốn, ở đây mình lấy all)
    @GetMapping
    public List<UserAccount> getAll() {
        return userRepo.findAll();
    }

    // 2. Xóa tài khoản
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        // Kiểm tra xem user có tồn tại không
        if(userRepo.existsById(id)) {
            userRepo.deleteById(id);
        } else {
            throw new RuntimeException("User không tồn tại!");
        }
    }
}