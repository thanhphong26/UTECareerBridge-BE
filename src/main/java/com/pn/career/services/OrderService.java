package com.pn.career.services;

import com.pn.career.dtos.RevenueReportDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.EmptyCartException;
import com.pn.career.exceptions.InvalidCouponException;
import com.pn.career.exceptions.InvalidOperationException;
import com.pn.career.models.*;
import com.pn.career.repositories.*;
import com.pn.career.responses.RecentOrderStatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final EmployerRepository employerRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartService cartService;
    private final EmployerPackageService employerPackageService;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Order saveOrder(Integer employerId, String couponCode) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
        Cart cart = cartRepository.findByEmployer(employer)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy giỏ hàng tương ứng"));

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Giỏ hàng trống");
        }

        Order order = new Order();
        order.setEmployer(employer);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setActive(true);
        List<OrderDetail> orderDetails=createOrderDetail(order,cart.getItems());
        order.setOrderDetails(orderDetails);
        BigDecimal total = calculateTotal(orderDetails);
        applyCoupon(order, couponCode, total);
        Order savedOrder = orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);

        // Clear the cart
        cartService.clearCart(employerId);

        return savedOrder;
    }
    private List<OrderDetail> createOrderDetail(Order order, List<CartItem> cartItems) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setJobPackage(cartItem.getJobPackage());
            orderDetail.setPrice(cartItem.getJobPackage().getPrice());
            orderDetail.setAmount(cartItem.getQuantity());
            orderDetails.add(orderDetail);
        }
        return orderDetails;
    }

    @Override
    public Page<Order> getOrdersByEmployerId(Integer employerId, Pageable pageable) {
        return orderRepository.findByEmployer_UserId(employerId, pageable);
    }

    @Override
    public Order getOrderById( Integer currentUserId, Integer orderId) {
        Order order = getOrder(orderId);
        User user=userRepository.findById(currentUserId)
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy người dùng tương ứng"));
        if(user.getRole().getRoleName().equals("employer") && currentUserId!=order.getEmployer().getUserId()){
            throw new InvalidOperationException("Không thể xem thông tin đơn hàng của người khác");
        }
        return order;
    }

    @Override
    @Transactional
    public Order updatePaymentStatus(Integer orderId, PaymentStatus paymentStatus) {
        Order order = getOrder(orderId);
        order.setPaymentStatus(paymentStatus);
        if (paymentStatus == PaymentStatus.PAID) {
            order.setPaymentDate(LocalDateTime.now());
            employerPackageService.createEmployerPackage(order);
        }
        return orderRepository.save(order);
    }
    @Override
    public List<OrderDetail> getOrderDetails(Integer orderId) {
        return orderDetailRepository.findByOrder_OrderId(orderId);
    }

    @Override
    @Transactional
    public void removeOrderDetail(Integer orderId, Integer orderDetailId) {
        Order order = getOrder(orderId);
        if (order.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new InvalidOperationException("Không thể chỉnh sửa đơn hàng đã thanh toán");
        }
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy chi tiết đơn hàng"));
        order.getOrderDetails().remove(orderDetail);
        orderDetailRepository.delete(orderDetail);
        updateOrderTotal(order);
    }
    private void updateOrderTotal(Order order) {
        BigDecimal newTotal = calculateTotal(order.getOrderDetails());
        if (order.getCoupon() != null) {
            BigDecimal discount = newTotal.multiply(BigDecimal.valueOf(order.getCoupon().getDiscount() / 100.0));
            newTotal = newTotal.subtract(discount);
        }
        order.setTotal(newTotal);
        orderRepository.save(order);
    }
    @Override
    public Order cancelOrder(Integer orderId) {
        Order order = getOrder(orderId);
        if (order.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new InvalidOperationException("Không thể hủy đơn hàng đã thanh toán");
        }
        order.setPaymentStatus(PaymentStatus.CANCELLED);
        order.setActive(false);
        return orderRepository.save(order);
    }

    @Override
    public Page<Order> getOrdersByPaymentStatus(String keyword, Integer employerId, LocalDate startDate, LocalDate endDate, PaymentStatus paymentStatus, PageRequest pageRequest) {
         return orderRepository.search(keyword,employerId,startDate,endDate,paymentStatus,pageRequest);
    }

    @Override
    public void deleteOrder(Integer orderId) {
        Order order = getOrder(orderId);
        if(order.getPaymentStatus()==PaymentStatus.PAID){
            throw new InvalidOperationException("Không thể xóa đơn hàng đã thanh toán");
        }
        orderRepository.delete(order);
    }
    @Override
    public RevenueReportDTO generateRevenueReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders=orderRepository.findByOrderDateBetweenAndPaymentStatus(startDate,endDate,PaymentStatus.PAID);
        BigDecimal totalRevenue=orders.stream()
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        Long orderCount= (long) orders.size();
        return RevenueReportDTO.builder()
                .totalRevenue(totalRevenue)
                .orderCount(orderCount)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    @Override
    public Page<RecentOrderStatResponse> getRecentOrderStats(LocalDateTime startDate, LocalDateTime endDate, PageRequest pageRequest) {
        Page<Order> recentOrders = orderRepository.findAllByOrderDateBetween(startDate, endDate, pageRequest);
        return recentOrders.map(order -> {
            OrderDetail firstOrderDetail = order.getOrderDetails().stream()
                    .findFirst()
                    .orElse(null);

            String packageName = firstOrderDetail != null ?
                    firstOrderDetail.getJobPackage().getPackageName() : "N/A";

            return RecentOrderStatResponse.builder()
                    .companyName(order.getEmployer().getCompanyName())
                    .packageName(packageName + (order.getOrderDetails().size() > 1 ?
                            " (+" + (order.getOrderDetails().size() - 1) + " khác)" : ""))
                    .purchaseDate(order.getOrderDate())
                    .price(order.getTotal().doubleValue())
                    .paymentStatus(order.getPaymentStatus().name())
                    .build();
        });
    }

    private BigDecimal calculateTotal(List<OrderDetail> orderDetails) {
        return orderDetails.stream()
                .map(detail -> BigDecimal.valueOf(detail.getPrice()).multiply(BigDecimal.valueOf(detail.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private void applyCoupon(Order order, String couponCode, BigDecimal total){
        if (couponCode != null) {
            Coupon coupon = couponRepository.findByCouponCode(couponCode);
            if (!coupon.isActive() || coupon.getExpiredAt().isBefore(java.time.LocalDateTime.now())) {
                throw new InvalidCouponException("Mã giảm giá đã hết hạn hoặc không còn hoạt động");
            }
            if (coupon.getMaxUsage() <= 0) {
                throw new InvalidCouponException("Mã giảm giá đã vượt quá số lần sử dụng tối đa");
            }
            BigDecimal discount = total.multiply(BigDecimal.valueOf(coupon.getDiscount() / 100.0));
            total = total.subtract(discount);
            order.setCoupon(coupon);
        }
        order.setTotal(total);
    }
    private Order getOrder(Integer orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đơn hàng tương ứng"));
    }
}
