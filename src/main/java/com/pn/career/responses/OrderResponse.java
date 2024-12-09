package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.models.Order;
import com.pn.career.models.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Integer orderId;
    private EmployerResponse employer;
    //if coupon is null, couponCode is null
    private String couponCode;
    private String accountNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime orderDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private PaymentStatus paymentStatus;
    private BigDecimal total;
    public static OrderResponse fromOrder(Order order){
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .employer(EmployerResponse.fromUser(order.getEmployer()))
                .couponCode(order.getCoupon()!=null?order.getCoupon().getCouponCode():null)
                .accountNumber(order.getAccountNumber())
                .orderDate(order.getOrderDate())
                .paymentStatus(order.getPaymentStatus())
                .total(order.getTotal())
                .build();
    }

}
