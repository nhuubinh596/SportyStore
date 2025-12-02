package com.example.backendquanaothethao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    private String description;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    // Map đúng tên cột trong SQL (image_url)
    @Column(name = "image_url")
    private String imageUrl;

    // --- QUAN TRỌNG: CẮT ĐỨT QUAN HỆ KHI GỬI JSON ---
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore // <--- Thêm dòng này: Không gửi cả cục Category về Frontend
    private Category category;

    // --- THÊM 2 HÀM NÀY ĐỂ FRONTEND VẪN LẤY ĐƯỢC TÊN DANH MỤC ---

    // Lấy ID danh mục (cho trang Sửa/Thêm)
    public Long getCategoryId() {
        return category != null ? category.getId() : null;
    }

    // Lấy Tên danh mục (cho trang Danh sách)
    public String getCategoryName() {
        return category != null ? category.getName() : "Chưa phân loại";
    }
}