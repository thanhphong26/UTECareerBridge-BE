package com.pn.career.controllers;

import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Cart;
import com.pn.career.models.CartItem;
import com.pn.career.responses.CartItemResponse;
import com.pn.career.responses.CartResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/carts")
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;
    @PostMapping("/add-to-cart")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> addPackageToCart(@AuthenticationPrincipal Jwt jwt, @RequestParam Integer packageId, @RequestParam Integer quantity) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        Cart cart = cartService.addPackageToCart(userId, packageId, quantity);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .data(CartResponse.fromCart(cart))
                .message("Thêm vào giỏ hàng thành công")
                .status(HttpStatus.OK)
                .build());
    }
    @PostMapping("/remove")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> removePackageFromCart(@AuthenticationPrincipal Jwt jwt, @RequestParam Integer packageId) throws DataNotFoundException {
        try{
            Long userIdLong = jwt.getClaim("userId");
            Integer userId = userIdLong != null ? userIdLong.intValue() : null;
            Cart cart = cartService.removePackageFromCart(userId, packageId);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .data(CartResponse.fromCart(cart))
                    .message("Xóa gói dịch vụ khỏi giỏ hàng thành công")
                    .status(HttpStatus.OK)
                    .build());
        }catch (Exception e){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .data(null)
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }
    @GetMapping("/get-cart")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getCartItems(@AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        List<CartItem> carts = cartService.getCartItems(userId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .data(carts.stream().map(CartItemResponse::fromCartItem).toList())
                .message("Lấy thông tin giỏ hàng thành công")
                .status(HttpStatus.OK)
                .build());
    }

}
