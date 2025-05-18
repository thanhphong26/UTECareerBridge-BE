package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "orders")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;
    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
    @Column(name = "account_number")
    private String accountNumber;
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    @Column(name="total",precision = 18, scale = 2)
    private BigDecimal total;
    @Column(name = "is_active")
    private boolean isActive;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderDetail> orderDetails;

}
