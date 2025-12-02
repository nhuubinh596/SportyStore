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

    // 1. Sửa tên biến thành 'qty' (để khớp với lệnh .setQty bên Controller)
    @Column(name = "qty")
    private int qty;

    // 2. Sửa kiểu dữ liệu thành BigDecimal (để khớp với lệnh .setPrice bên Controller)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}