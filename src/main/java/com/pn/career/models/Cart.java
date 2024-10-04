package com.pn.career.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cart extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private List<CartItem> items = new ArrayList<>();
}
