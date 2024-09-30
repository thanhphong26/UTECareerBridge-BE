package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    @Column(name="phone_number")
    private String phoneNumber;
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name="gender")
    private boolean gender;
    @Column(name="dob")
    private LocalDate dob;
    @Column(name="province_id")
    private int provinceId;
    @Column(name = "district_id")
    private int districtId;
    @Column(name="ward_id")
    private int wardId;
    @Column(name="address")
    private String address;
    @Column(name = "is_active")
    private boolean active=true;
    @Column(name = "reason_blocked")
    private String reasonBlocked;
    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens;
}
