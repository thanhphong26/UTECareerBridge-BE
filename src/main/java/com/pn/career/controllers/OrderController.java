package com.pn.career.controllers;

import com.pn.career.configurations.VNPAYConfig;
import com.pn.career.exceptions.InvalidOperationException;
import com.pn.career.models.Order;
import com.pn.career.models.OrderDetail;
import com.pn.career.models.PaymentStatus;
import com.pn.career.responses.OrderDetailResponse;
import com.pn.career.responses.OrderListResponse;
import com.pn.career.responses.OrderResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IOrderService;
import com.pn.career.services.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final VNPAYService vnpayService;

    @PostMapping("/create-payment")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> createPayment(@AuthenticationPrincipal Jwt jwt, @RequestParam Integer orderId, HttpServletRequest request){
        Long userIdLong = jwt.getClaim("userId");
        Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
        Order order = orderService.getOrderById(employerId, orderId);

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":3000";
        String returnUrl = baseUrl  + VNPAYConfig.vnp_Returnurl;

        String paymentUrl = vnpayService.createPaymentUrl(request, order, returnUrl);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tạo URL thanh toán thành công")
                .status(HttpStatus.OK)
                .data(paymentUrl)
                .build());
    }
    @GetMapping("/vnpay-payment-return")
    public ResponseEntity<ResponseObject> vnpayReturn(HttpServletRequest request) {
        int paymentStatus =vnpayService.orderReturn(request);
        if (paymentStatus==1) {
            String orderId = request.getParameter("vnp_OrderInfo").split(": ")[1];
            orderService.updatePaymentStatus(Integer.parseInt(orderId), PaymentStatus.PAID);

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Thanh toán thành công")
                    .status(HttpStatus.OK)
                    .build());
        } else {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Thanh toán thất bại")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }
    @PostMapping("/create-order")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> createOrder(@AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) String couponCode){
        Long userIdLong = jwt.getClaim("userId");
        Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
        Order order = orderService.saveOrder(employerId, couponCode);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Mua gói dịch vụ thành công")
                .status(HttpStatus.CREATED)
                .data(OrderResponse.fromOrder(order))
                .build());
    }
    @GetMapping("/get-orders")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getOrder(@AuthenticationPrincipal Jwt jwt, @RequestParam Integer page, @RequestParam Integer limit){
        Long userIdLong = jwt.getClaim("userId");
        Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<Order> orders = orderService.getOrdersByEmployerId(employerId, pageRequest);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách đơn hàng thành công")
                .status(HttpStatus.OK)
                .data(OrderListResponse.builder()
                        .orders(orders.map(OrderResponse::fromOrder).toList())
                        .totalPage(orders.getTotalPages())
                        .build())
                .build());
    }
    @GetMapping("/get-order/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getOrder(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer orderId){
        Long userIdLong = jwt.getClaim("userId");
        Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
        Order order = orderService.getOrderById(employerId, orderId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thông tin đơn hàng thành công")
                .status(HttpStatus.OK)
                .data(OrderResponse.fromOrder(order))
                .build());
    }
    @PutMapping("/update-payment-status/{orderId}/payment-status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updatePaymentStatus(@PathVariable Integer orderId, @RequestParam PaymentStatus paymentStatus){
        Order order = orderService.updatePaymentStatus(orderId, paymentStatus);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Cập nhật trạng thái thanh toán thành công")
                .status(HttpStatus.OK)
                .data(OrderResponse.fromOrder(order))
                .build());
    }
    @GetMapping("/get-order-details/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getOrderDetails(@PathVariable Integer orderId){
        List<OrderDetail> orderResponses = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thông tin chi tiết đơn hàng thành công")
                .status(HttpStatus.OK)
                .data(orderResponses.stream().map(OrderDetailResponse::fromOrderDetail).toList())
                .build());
    }
    @DeleteMapping("/remove-order-detail/{orderId}/order-detail/{orderDetailId}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> removeOrderDetail(@PathVariable Integer orderId, @PathVariable Integer orderDetailId){
       try{
           orderService.removeOrderDetail(orderId, orderDetailId);
           return ResponseEntity.ok().body(ResponseObject.builder()
                   .message("Xóa chi tiết đơn hàng thành công")
                   .status(HttpStatus.OK)
                   .build());
       }catch(InvalidOperationException e){
              return ResponseEntity.badRequest().body(ResponseObject.builder()
                     .message(e.getMessage())
                     .status(HttpStatus.BAD_REQUEST)
                     .build());
       }catch(Exception e){
              return ResponseEntity.badRequest().body(ResponseObject.builder()
                     .message("Xóa chi tiết đơn hàng thất bại")
                     .status(HttpStatus.BAD_REQUEST)
                     .build());
       }
    }
    @PutMapping("/cancel-order/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> cancelOrder(@PathVariable Integer orderId){
        Order order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Hủy đơn hàng thành công")
                .status(HttpStatus.OK)
                .data(OrderResponse.fromOrder(order))
                .build());
    }
    @GetMapping("/get-orders-by-payment-status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getOrdersByPaymentStatus(@RequestParam(defaultValue = "0") @Min(0) Integer page,
                                                                   @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
                                                                   @RequestParam(required = false) String keyword, @RequestParam(required = false) Integer employerId,
                                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
                                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate,
                                                                   @RequestParam(required = false) PaymentStatus paymentStatus){
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<Order> orders = orderService.getOrdersByPaymentStatus(keyword, employerId, startDate, endDate, paymentStatus, pageRequest);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách đơn hàng theo trạng thái thanh toán thành công")
                .status(HttpStatus.OK)
                .data(OrderListResponse.builder()
                        .orders(orders.map(OrderResponse::fromOrder).toList())
                        .totalPage(orders.getTotalPages())
                        .count(orders.getTotalElements())
                        .build())
                .build());
    }
    @DeleteMapping("/delete-order/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteOrder(@PathVariable Integer orderId){
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Xóa đơn hàng thành công")
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/generate-revenue-report")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> generateRevenueReport(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate){
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tạo báo cáo doanh thu thành công")
                .status(HttpStatus.OK)
                .data(orderService.generateRevenueReport(startDate, endDate))
                .build());
    }
}
