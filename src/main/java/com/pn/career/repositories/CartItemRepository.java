package com.pn.career.repositories;

import com.pn.career.models.Cart;
import com.pn.career.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{
    List<CartItem> findByCart(Cart cart);
}
