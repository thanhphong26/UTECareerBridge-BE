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
    @Column(name = "role_id")
    private int roleId;
    @Column(name = "role_name", length = 100, nullable = false)
    private String roleName;
    private static String ADMIN="ADMIN";
    private static String STUDENT="STUDENT";
    private static String EMPLOYER="EMPLOYER";
}
