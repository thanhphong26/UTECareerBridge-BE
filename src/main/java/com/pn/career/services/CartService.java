package com.pn.career.services;

import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.InvalidCouponException;
import com.pn.career.models.*;
import com.pn.career.models.Package;
import com.pn.career.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PackageRepository packageRepository;
    private final EmployerRepository employerRepository;
    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public Cart addPackageToCart(Integer employerId, Integer packageId, Integer quantity) {
        Employer employer=getEmployer(employerId);
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
    @Transactional
    public Cart removePackageFromCart(Integer employerId, Integer packageId) {
        Employer employer=getEmployer(employerId);
        Cart cart = cartRepository.findByEmployer(employer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().removeIf(item -> item.getJobPackage().getPackageId() == packageId);
        return cartRepository.save(cart);
    }
  /*  @Override
    public Cart applyCoupon(Integer employerId, String couponCode) {
        Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
        Cart cart = cartRepository.findByEmployer(employer)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy giỏ hàng tương ứng"));
        Coupon coupon = couponRepository.findByCouponCode(couponCode);

        if (!coupon.isActive() || coupon.getExpiredAt().isBefore(java.time.LocalDateTime.now())) {
            throw new InvalidCouponException("Mã giảm giá đã hết hạn hoặc không còn hoạt động");
        }
        if (coupon.getMaxUsage() <= 0) {
            throw new InvalidCouponException("Mã giảm giá đã vượt quá số lần sử dụng tối đa");
        }
        updateCartTotal(cart);

        coupon.setMaxUsage(coupon.getMaxUsage() - 1);
        couponRepository.save(coupon);

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeCoupon(Integer employerId) {
        return null;
    }*/

    @Override
    public void clearCart(Integer employerId) {
        Employer employer=getEmployer(employerId);
        Cart cart = cartRepository.findByEmployer(employer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public List<CartItem> getCartItems(Integer employerId) {
        Employer employer=getEmployer(employerId);
        Cart cart = cartRepository.findByEmployer(employer)
                .orElseThrow(() -> new DataNotFoundException("Giỏ hàng của bạn chưa thêm dịch vụ nào"));
        return cart.getItems();
    }
    private Employer getEmployer(Integer employerId) {
        return employerRepository.findById(employerId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
    }
}
