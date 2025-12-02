package com.example.backendquanaothethao.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // Import cái này
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "category")
    @JsonIgnore // <--- THÊM DÒNG NÀY ĐỂ CHẶN LỖI 500
    private List<Product> products;
}