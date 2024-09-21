package com.pn.career.controllers;
import com.pn.career.components.LocalizationUtils;
import com.pn.career.dtos.IndustryDTO;
import com.pn.career.dtos.IndustryUpdateDTO;
import com.pn.career.models.Industry;
import com.pn.career.responses.IndustryResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IIndustryService;
import com.pn.career.utils.MessageKeys;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/industries")
@AllArgsConstructor
public class IndustryController {
    private final Logger logger = LoggerFactory.getLogger(IndustryController.class);
    private final IIndustryService industryService;
    private final LocalizationUtils localizationUtils;
    @GetMapping("/get-all-industries")
    public ResponseEntity<ResponseObject> getAllIndustries() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
        boolean isAdmin = isAuthenticated && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<Industry> industries = industryService.getAllActiveIndustries(isAdmin);
        List<IndustryResponse> industryResponses = industries.stream()
                .map(IndustryResponse::fromIndustry)
                .toList();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách loại hình công ty thành công")
                .status(HttpStatus.OK)
                .data(industryResponses)
                .build());
    }
    @GetMapping("/{industryId}")
    public ResponseEntity<ResponseObject> getIndustryById(@PathVariable Integer industryId) {
        Industry industry = industryService.getIndustryById(industryId);
        IndustryResponse industryResponse = IndustryResponse.fromIndustry(industry);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thông tin loại hình công ty thành công")
                .status(HttpStatus.OK)
                .data(industryResponse)
                .build());
    }
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createIndustry(@RequestBody IndustryDTO industry) {
        Industry newIndustry = industryService.createIndustry(industry);
        IndustryResponse industryResponse = IndustryResponse.fromIndustry(newIndustry);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Thêm mới loại hình công ty thành công")
                .status(HttpStatus.OK)
                .data(industryResponse)
                .build());
    }
    @PutMapping("/{industryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateIndustry(@PathVariable Integer industryId, @RequestBody IndustryUpdateDTO industryDTO) {
        Industry updatedIndustry = industryService.updateIndustry(industryId, industryDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Cập nhật loại hình công ty thành công")
                .status(HttpStatus.OK)
                .data(updatedIndustry)
                .build());
    }
    @DeleteMapping("/{industryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteIndustry(@PathVariable Integer industryId) {
        industryService.deleteIndustry(industryId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Xóa loại hình công ty thành công")
                .status(HttpStatus.OK)
                .build());
    }

}
