package com.example.backendquanaothethao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable=false, length=50, unique=true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore // <--- QUAN TRỌNG: Cắt đứt vòng lặp từ phía Role về User
    private Set<UserAccount> users;

}
