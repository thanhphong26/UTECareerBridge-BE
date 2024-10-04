package com.pn.career.services;

import com.pn.career.models.Order;
import com.pn.career.models.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    Order saveOrder(Order order);
    Page<Order> getOrdersByEmployerId(Integer employerId, Pageable pageable);
    Order getOrderById(Integer orderId);
    Order updatePaymentStatus(Integer orderId, boolean status);
    List<OrderDetail> getOrderDetails(Integer orderId);
    OrderDetail saveOrderDetail(Integer orderId, OrderDetail orderDetail);
    void removeOrderDetail(Integer orderId, Integer orderDetailId);

}
