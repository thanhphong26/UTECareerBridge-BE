package com.pn.career.services;

import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Cart;
import com.pn.career.models.CartItem;
import com.pn.career.models.Employer;
import com.pn.career.models.Package;
import com.pn.career.repositories.CartItemRepository;
import com.pn.career.repositories.CartRepository;
import com.pn.career.repositories.EmployerRepository;
import com.pn.career.repositories.PackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PackageRepository packageRepository;
    private final EmployerRepository employerRepository;

    @Override
    public Cart addPackageToCart(Integer employerId, Integer packageId, Integer quantity) {
        Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
        Package jobPackage=packageRepository.findById(packageId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy gói tương ứng"));
        Cart cart = cartRepository.findByEmployer(employer)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setEmployer(employer);
                    return cartRepository.save(newCart);
                });
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getJobPackage().getPackageId() == packageId)
                .findFirst();
        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setJobPackage(jobPackage);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }
       return cartRepository.save(cart);
    }

    @Override
    public Cart removePackageFromCart(Integer employerId, Integer packageId) {
        Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
        Cart cart = cartRepository.findByEmployer(employer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().removeIf(item -> item.getJobPackage().getPackageId() == packageId);
        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(Integer employerId) {
        Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
        Cart cart = cartRepository.findByEmployer(employer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public List<CartItem> getCartItems(Integer employerId) {
        Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
        Cart cart = cartRepository.findByEmployer(employer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return cart.getItems();
    }

}
