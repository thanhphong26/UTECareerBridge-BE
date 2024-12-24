package com.pn.career.controllers;
import com.pn.career.dtos.SkillDTO;
import com.pn.career.dtos.SkillUpdateDTO;
import com.pn.career.models.Skill;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.ISkillService;
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
@RequestMapping("${api.prefix}/skills")
@AllArgsConstructor
public class SkillController {
    private final ISkillService skillService;
    @GetMapping("/get-all-skills")
    public ResponseEntity<ResponseObject> getAllSkills(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
        boolean isAdmin = isAuthenticated && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<Skill> skills = skillService.findAllSkills(isAdmin);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy danh sách các kỹ năng thành công")
                .data(skills)
                .build());
    }
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createSkill(@RequestBody SkillDTO skillDTO){
        Skill skill = skillService.createSkill(skillDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Thêm mới kỹ năng thành công")
                .data(skill)
                .build());
    }
    @GetMapping("/{skillId}")
    public ResponseEntity<ResponseObject> getSkillById(@PathVariable Integer skillId){
        Skill skill = skillService.getSkillById(skillId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy thông tin kỹ năng thành công")
                .data(skill)
                .build());
    }
    @PutMapping("/{skillId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateSkill(@PathVariable Integer skillId, @RequestBody SkillUpdateDTO skillDTO){
        Skill skill = skillService.updateSkill(skillId, skillDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Cập nhật kỹ năng thành công")
                .data(skill)
                .build());
    }
    @DeleteMapping("/{skillId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteSkill(@PathVariable Integer skillId){
        Skill skill = null;
        try {
            skill = skillService.deleteSkill(skillId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Xóa kỹ năng thành công")
                .data(skill)
                .build());
    }

}
