package com.pn.career.controllers;

import com.pn.career.models.Skill;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.ISkillService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/skills")
@AllArgsConstructor
public class SkillController {
    private final ISkillService skillService;
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
}
