// src/main/java/com/example/backendquanaothethao/service/impl/AuthServiceImpl.java
package com.example.backendquanaothethao.service;

import com.example.backendquanaothethao.model.UserAccount;
import com.example.backendquanaothethao.repository.UserRepository;
import com.example.backendquanaothethao.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserAccount authenticate(String username, String rawPassword) {
        Optional<UserAccount> opt = userRepo.findByUsername(username); // adjust method if different
        if (opt.isEmpty()) return null;
        UserAccount u = opt.get();
        String stored = u.getPassword();
        // if stored is hashed with bcrypt
        if (stored != null && stored.startsWith("$2a$")) {
            if (passwordEncoder.matches(rawPassword, stored)) return u;
            return null;
        }
        // fallback: plain text (not recommended)
        if (rawPassword.equals(stored)) return u;
        return null;
    }
}
