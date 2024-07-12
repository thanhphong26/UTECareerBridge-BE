package com.pn.career.models;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "roles")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    private static String ADMIN="ADMIN";
    private static String STUDENT="STUDENT";
    private static String EMPLOYER="EMPLOYER";
}
