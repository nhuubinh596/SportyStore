package com.example.backendquanaothethao.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal; // Import cái này để dùng tiền tệ chuẩn

@Data
@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qty")
    private int qty;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}