package com.example.backendquanaothethao.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating; // 1 đến 5 sao

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String comment; // Nội dung bình luận

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Quan hệ với User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;

    // Quan hệ với Product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}