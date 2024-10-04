package com.pn.career.controllers;

import com.pn.career.dtos.PackageDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Package;
import com.pn.career.responses.PackageResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/packages")
@RequiredArgsConstructor
public class PackageController {
    private final IPackageService packageService;
    @GetMapping("/get-all")
    public ResponseEntity<ResponseObject> getAllPackages(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
        boolean isAdmin = isAuthenticated && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<Package> packages = packageService.getAllPackages(isAdmin);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy danh sách các gói dịch vụ thành công")
                .data(packages.stream().map(PackageResponse::from).toList())
                .build());
    }
    @GetMapping("/get-package/{packageId}")
    public ResponseEntity<ResponseObject> getPackageById(@PathVariable Integer packageId){
        try{
            Package pkg=packageService.getPackageById(packageId);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Lấy thông tin gói dịch vụ thành công")
                    .status(HttpStatus.OK)
                    .data(PackageResponse.from(pkg))
                    .build());
        }catch(DataNotFoundException ex){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(ex.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .data(null)
                    .build());
        }catch (Exception ex){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(ex.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(null)
                    .build());
        }
    }
    @PostMapping("/create-package")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createPackage(@RequestBody PackageDTO packageDTO){
        try{
            Package pkg=packageService.createPackage(packageDTO);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Tạo gói dịch vụ mới thành công")
                    .status(HttpStatus.OK)
                    .data(PackageResponse.from(pkg))
                    .build());
        }catch (DataNotFoundException ex){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(ex.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .data(null)
                    .build());
        }catch (Exception ex){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(ex.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(null)
                    .build());
        }
    }
    @PutMapping("/update-package/{packageId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updatePackage(@PathVariable Integer packageId, @RequestBody PackageDTO packageDTO){
        try{
            Package pkg=packageService.updatePackage(packageId, packageDTO);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Cập nhật thông tin gói dịch vụ thành công")
                    .status(HttpStatus.OK)
                    .data(PackageResponse.from(pkg))
                    .build());
        }catch (DataNotFoundException ex){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(ex.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .data(null)
                    .build());
        }catch (Exception ex){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(ex.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(null)
                    .build());

        }
    }
    @DeleteMapping("/delete-package/{packageId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deletePackage(@PathVariable Integer packageId){
        packageService.deletePackage(packageId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Xóa gói dịch vụ thành công")
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }
}
