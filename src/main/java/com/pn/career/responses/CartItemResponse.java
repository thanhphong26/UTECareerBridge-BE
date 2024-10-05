package com.pn.career.responses;

import com.pn.career.models.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {
    private Long cartItemId;
    private PackageResponse jobPackage;
    private int quantity;
    public static CartItemResponse fromCartItem(CartItem cartItem){
        return CartItemResponse.builder()
                .cartItemId(cartItem.getCartItemId())
                .jobPackage(PackageResponse.from(cartItem.getJobPackage()))
                .quantity(cartItem.getQuantity())
                .build();
    }
}
