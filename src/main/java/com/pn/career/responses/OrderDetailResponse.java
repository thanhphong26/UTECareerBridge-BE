package com.pn.career.responses;

import com.pn.career.models.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponse {
    private Integer detailId;
    private Integer orderId;
    private PackageResponse packageResponse;
    private float price;
    private int amount;
    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail){
        return OrderDetailResponse.builder()
                .detailId(orderDetail.getDetailId())
                .orderId(orderDetail.getOrder().getOrderId())
                .packageResponse(PackageResponse.from(orderDetail.getJobPackage()))
                .price(orderDetail.getPrice())
                .amount(orderDetail.getAmount())
                .build();
    }
}
