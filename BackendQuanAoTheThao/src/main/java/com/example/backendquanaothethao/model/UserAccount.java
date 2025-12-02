package com.example.backendquanaothethao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "user_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username", nullable=false, unique=true, length=120)
    private String username;

    @Column(name="name", nullable=false, length=150)
    private String name;

    @Column(name="email", length=200)
    private String email;

    @Column(name="password", nullable=false, length=255)
    private String password;

    @Column(name="phone", length=50)
    private String phone;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    @JsonIgnore // <--- Thêm dòng này
    private List<Order> orders;

    // CẮT VÒNG LẶP 2: Quan hệ với Address (nếu có)
    @OneToMany(mappedBy = "user")
    @JsonIgnore // <--- Thêm dòng này
    private List<Address> addresses;

    // Quan hệ với Role (Thường không gây crash nhưng nên cẩn thận)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // handy getter for names (used by controller to return simple list)
    public List<String> getRoleNames() {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}
