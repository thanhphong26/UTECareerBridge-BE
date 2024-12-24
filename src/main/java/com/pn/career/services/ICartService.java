package com.pn.career.services;

import com.pn.career.models.Cart;
import com.pn.career.models.CartItem;
import java.util.List;

public interface ICartService {
    Cart addPackageToCart(Integer employerId, Integer packageId, Integer quantity);
    Cart removePackageFromCart(Integer employerId, Integer packageId);
    void clearCart(Integer employerId);
    List<CartItem> getCartItems(Integer employerId);
}
