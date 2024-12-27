package com.pn.career.responses;

import com.pn.career.models.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private Long cartId;
    private EmployerResponse employer;
    private List<CartItemResponse> items;
    public static CartResponse fromCart(Cart cart){
        return CartResponse.builder()
                .cartId(cart.getCartId())
                .employer(EmployerResponse.fromUser(cart.getEmployer()))
                .items(cart.getItems().stream().map(CartItemResponse::fromCartItem).toList())
                .build();
    }
}
