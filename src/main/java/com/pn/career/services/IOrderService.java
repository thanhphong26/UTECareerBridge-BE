package com.pn.career.services;

import com.pn.career.dtos.RevenueReportDTO;
import com.pn.career.models.Order;
import com.pn.career.models.OrderDetail;
import com.pn.career.models.PaymentStatus;
import com.pn.career.responses.RecentOrderStatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IOrderService {
    Order saveOrder(Integer employerId, String couponCode);
    Page<Order> getOrdersByEmployerId(Integer employerId, Pageable pageable);
    Order getOrderById( Integer currentUserId, Integer orderId);
    Order updatePaymentStatus(Integer orderId, PaymentStatus paymentStatus);
    List<OrderDetail> getOrderDetails(Integer orderId);
    void removeOrderDetail(Integer orderId, Integer orderDetailId);
    Order cancelOrder(Integer orderId);
    Page<Order> getOrdersByPaymentStatus(String keyword, Integer employerId, LocalDate startDate, LocalDate endDate, PaymentStatus paymentStatus, PageRequest pageRequest);
    void deleteOrder(Integer orderId);
    RevenueReportDTO generateRevenueReport(LocalDateTime startDate, LocalDateTime endDate);
    Page<RecentOrderStatResponse> getRecentOrderStats(LocalDateTime startDate, LocalDateTime endDate, PageRequest pageRequest);
}
