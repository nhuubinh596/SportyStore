package com.example.backendquanaothethao.repository;

import com.example.backendquanaothethao.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Tìm role theo tên (ví dụ để tìm "ROLE_USER" cho chắc ăn)
    Optional<Role> findByName(String name);
}