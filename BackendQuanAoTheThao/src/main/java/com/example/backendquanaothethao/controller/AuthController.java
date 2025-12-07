package com.example.backendquanaothethao.controller;

import com.example.backendquanaothethao.dto.RegisterRequest;
import com.example.backendquanaothethao.model.Role;
import com.example.backendquanaothethao.model.UserAccount;
import com.example.backendquanaothethao.dto.LoginRequest;
import com.example.backendquanaothethao.repository.RoleRepository;
import com.example.backendquanaothethao.repository.UserRepository;
import com.example.backendquanaothethao.service.AuthServiceImpl;
import com.example.backendquanaothethao.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthServiceImpl authService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthController(AuthServiceImpl authService, TokenService tokenService, UserRepository userRepository, RoleRepository roleRepository) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        UserAccount u = authService.authenticate(req.getUsername(), req.getPassword());
        if (u == null) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Invalid credentials"));
        }
        String token = tokenService.createTokenFor(u);
        var userView = Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "name", u.getName(),
                "email", u.getEmail(),
                "roles", u.getRoleNames() // List<String>
        );

        return ResponseEntity.ok(Map.of("success", true, "user", userView, "token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tên đăng nhập đã tồn tại!"));
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email này đã được sử dụng!"));
        }
        UserAccount u = new UserAccount();
        u.setUsername(req.getUsername());
        u.setPassword(req.getPassword());
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        u.setCreatedAt(java.time.LocalDateTime.now());
        Role roleUser = roleRepository.findByName("ROLE_USER").orElse(null);
        if (roleUser == null) {
            roleUser = roleRepository.findById(2L).orElse(null);
        }

        if (roleUser != null) {
            u.setRoles(java.util.Collections.singleton(roleUser));
        }
        userRepository.save(u);
        return ResponseEntity.ok(Map.of("message", "Đăng ký thành công!"));
    }
}
