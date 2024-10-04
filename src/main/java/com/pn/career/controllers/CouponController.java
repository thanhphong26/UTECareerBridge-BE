package com.pn.career.controllers;

import com.pn.career.dtos.CouponDTO;
import com.pn.career.models.Coupon;
import com.pn.career.responses.CouponListResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.ICouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("${api.prefix}/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final ICouponService couponService;
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createCoupon(@RequestBody CouponDTO couponDTO){
        try{
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Thêm mới mã giảm giá thành công")
                    .status(HttpStatus.OK)
                    .data(couponService.createCoupon(couponDTO))
                    .build());
        }catch (Exception e){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }
    @GetMapping("/{couponId}")
    public ResponseEntity<ResponseObject> getCouponById(@PathVariable Integer couponId){
        try{
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Lấy mã giảm giá thành công")
                    .status(HttpStatus.OK)
                    .data(couponService.getCouponById(couponId))
                    .build());
        }catch (Exception e){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build());
        }
    }
    @GetMapping("/get-all")
    public ResponseEntity<ResponseObject> getAllCoupons(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
        boolean isAdmin = isAuthenticated && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Coupon> coupons = couponService.getAllCoupons(isAdmin, pageRequest);
        if(coupons.isEmpty()){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Không tìm thấy mã giảm giá")
                    .status(HttpStatus.NOT_FOUND)
                    .build());
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách mã giảm giá thành công")
                .status(HttpStatus.OK)
                .data(CouponListResponse.builder()
                        .couponList(coupons.getContent())
                        .totalPages(coupons.getTotalPages())
                        .build()
                )
                .build());
    }
    @PutMapping("/{couponId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateCoupon(@PathVariable Integer couponId, @RequestBody CouponDTO coupon){
        try{
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Cập nhật mã giảm giá thành công")
                    .status(HttpStatus.OK)
                    .data(couponService.updateCoupon(couponId, coupon))
                    .build());
        }catch (Exception e){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }
    @DeleteMapping("/{couponId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteCoupon(@PathVariable Integer couponId){
        try{
            couponService.deleteCoupon(couponId);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Xóa mã giảm giá thành công")
                    .status(HttpStatus.OK)
                    .build());
        }catch (Exception e){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

}
