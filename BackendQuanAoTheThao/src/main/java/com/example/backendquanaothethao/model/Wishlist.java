package com.example.backendquanaothethao.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "wishlist")
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Ai thích?
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;

    // Thích cái gì?
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}