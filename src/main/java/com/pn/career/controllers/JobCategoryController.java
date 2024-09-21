package com.pn.career.controllers;

import com.pn.career.dtos.CategoryJobDTO;
import com.pn.career.dtos.CategoryJobUpdateDTO;
import com.pn.career.models.JobCategory;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IJobCategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.hibernate.boot.archive.scan.spi.ClassDescriptor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/job-categories")
@AllArgsConstructor
public class JobCategoryController {
    private final IJobCategoryService jobCategoryService;
    @GetMapping("/get-all-job-categories")
    public ResponseEntity<ResponseObject> getAllJobCategories(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
        boolean isAdmin = isAuthenticated && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<JobCategory> jobCategories = jobCategoryService.findAllJobCategories(isAdmin);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy danh sách các ngành nghề thành công")
                .data(jobCategories)
                .build());
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<ResponseObject> createJobCategory(@Valid @RequestBody CategoryJobDTO categoryJobDTO, BindingResult result){
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(errorMessages.toString())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
        try{
            JobCategory jobCategory = jobCategoryService.createJobCategory(categoryJobDTO);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Tạo ngành nghề mới thành công")
                    .status(HttpStatus.OK)
                    .data(jobCategory)
                    .build());
        }catch(DuplicateKeyException e){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.CONFLICT)
                    .build());
        }catch(Exception e){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Đã có lỗi xảy ra trong khi thực hiện thêm mới ngành nghề")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }
    @GetMapping("/{jobCategoryId}")
    public ResponseEntity<ResponseObject> getJobCategoryById(@PathVariable Integer jobCategoryId){
        JobCategory jobCategory = jobCategoryService.getJobCategoryById(jobCategoryId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy thông tin ngành nghề thành công")
                .data(jobCategory)
                .build());
    }
    @PutMapping("/{jobCategoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateJobCategory(@PathVariable Integer jobCategoryId, @RequestBody CategoryJobUpdateDTO categoryJobDTO){
        JobCategory jobCategory = jobCategoryService.updateJobCategory(jobCategoryId, categoryJobDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Cập nhật ngành nghề thành công")
                .data(jobCategory)
                .build());
    }
    @DeleteMapping("/{jobCategoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteJobCategory(@PathVariable Integer jobCategoryId){
        try{
            jobCategoryService.deleteJobCategory(jobCategoryId);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Xóa ngành nghề thành công")
                    .build());
        }catch(Exception e){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Đã có lỗi xảy ra trong khi xóa ngành nghề")
                    .build());
        }
    }
}
