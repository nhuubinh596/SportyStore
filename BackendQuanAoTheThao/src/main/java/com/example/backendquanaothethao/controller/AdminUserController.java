package com.example.backendquanaothethao.controller;

import com.example.backendquanaothethao.model.UserAccount;
import com.example.backendquanaothethao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort; // Import thêm
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminUserController {

    @Autowired
    private UserRepository userRepo;

    // Sửa lại: Lấy danh sách user có sắp xếp theo ID giảm dần (mới nhất lên đầu)
    @GetMapping
    public List<UserAccount> getAll() {
        return userRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        if(userRepo.existsById(id)) {
            userRepo.deleteById(id);
        } else {
            throw new RuntimeException("User không tồn tại!");
        }
    }
}