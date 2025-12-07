package com.example.backendquanaothethao.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String street;
    private String city;
    private String province;
    private String postalCode;
    private String phone;
    private Boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;
}