package com.pn.career.controllers;

import com.pn.career.models.CVTemplate;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.ICVTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/cv-templates")
@RequiredArgsConstructor
public class CVTemplateController {
    private final ICVTemplateService cvTemplateService;
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getOneTemplateById(@PathVariable Integer id){
        try{
            CVTemplate cvTemplate = cvTemplateService.findCVTemplateById(id);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Lấy CV thành công")
                    .status(HttpStatus.OK)
                    .data(cvTemplate)
                    .build());
        }catch (Exception e){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build());
        }
    }
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createCVTemplate(@RequestBody CVTemplate cvTemplate){
        try{
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Thêm mới CV thành công")
                    .status(HttpStatus.OK)
                    .data(cvTemplateService.saveCVTemplate(cvTemplate))
                    .build());
        }catch (Exception e){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteCVTemplate(@PathVariable Integer id){
        try{
            cvTemplateService.deleteCVTemplate(id);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Xóa CV thành công")
                    .status(HttpStatus.OK)
                    .build());
        }catch (Exception e){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }

    }
    @GetMapping("/get-all")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getAllCVTemplates(){
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách CV thành công")
                .status(HttpStatus.OK)
                .data(cvTemplateService.findAllCVTemplates())
                .build());
    }

}
