package com.pn.career.services;

import com.pn.career.models.Order;
import com.pn.career.models.OrderDetail;
import com.pn.career.repositories.OrderDetailRepository;
import com.pn.career.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    @Override
    public Order saveOrder(Order order) {
        return null;
    }

    @Override
    public Page<Order> getOrdersByEmployerId(Integer employerId, Pageable pageable) {
        return null;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        return null;
    }

    @Override
    public Order updatePaymentStatus(Integer orderId, boolean status) {
        return null;
    }

    @Override
    public List<OrderDetail> getOrderDetails(Integer orderId) {
        return List.of();
    }

    @Override
    public OrderDetail saveOrderDetail(Integer orderId, OrderDetail orderDetail) {
        return null;
    }

    @Override
    public void removeOrderDetail(Integer orderId, Integer orderDetailId) {

    }
}
