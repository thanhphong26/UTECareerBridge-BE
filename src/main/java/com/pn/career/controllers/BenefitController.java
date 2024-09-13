package com.pn.career.controllers;

import com.pn.career.models.Benefit;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IBenefitService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/benefits")
@AllArgsConstructor
public class BenefitController {
    private final IBenefitService benefitService;
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
}
