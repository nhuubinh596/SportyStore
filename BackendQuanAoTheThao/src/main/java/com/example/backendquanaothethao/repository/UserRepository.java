package com.example.backendquanaothethao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backendquanaothethao.model.UserAccount;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
