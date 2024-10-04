package com.pn.career.controllers;

import com.pn.career.dtos.BenefitDTO;
import com.pn.career.dtos.BenefitUpdateDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Benefit;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IBenefitDetailService;
import com.pn.career.services.IBenefitService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/benefits")
@AllArgsConstructor
public class BenefitController {
    private final IBenefitService benefitService;
    private final IBenefitDetailService benefitDetailService;
    @GetMapping("/get-all-benefits")
    public ResponseEntity<ResponseObject> getAllBenefits(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
        boolean isAdmin = isAuthenticated && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<Benefit> benefits=benefitService.findAllBenefits(isAdmin);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách các phúc lợi từ công ty thành công")
                .status(HttpStatus.OK)
                .data(benefits)
                .build());
    }
    @GetMapping("/{benefitId})")
    public ResponseEntity<ResponseObject> getBenefitById(@PathVariable  Integer benefitId) throws DataNotFoundException {
        Benefit benefit=benefitService.getBenefitById(benefitId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thông tin phúc lợi thành công")
                .status(HttpStatus.OK)
                .data(benefit)
                .build());
    }
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createBenefit(@RequestBody BenefitDTO benefitDTO){
        Benefit benefit=benefitService.createBenefit(benefitDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Thêm mới phúc lợi thành công")
                .status(HttpStatus.OK)
                .data(benefit)
                .build());
    }
    @PutMapping("/{benefitId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateBenefit(@PathVariable Integer benefitId, @RequestBody BenefitUpdateDTO benefitDTO){
        Benefit benefit=benefitService.updateBenefit(benefitId, benefitDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Cập nhật phúc lợi thành công")
                .status(HttpStatus.OK)
                .data(benefit)
                .build());
    }
    @DeleteMapping("/{benefitId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteBenefit(@PathVariable Integer benefitId){
        try {
            benefitService.deleteBenefit(benefitId);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Xóa phúc lợi thành công")
                    .status(HttpStatus.OK)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }


}
