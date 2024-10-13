package com.pn.career.controllers;

import com.pn.career.dtos.ResumeDTO;
import com.pn.career.models.Application;
import com.pn.career.models.Resume;
import com.pn.career.models.StudentSkill;
import com.pn.career.responses.ApplicationResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.responses.ResumeResponse;
import com.pn.career.responses.StudentSkillResponse;
import com.pn.career.services.IApplicationService;
import com.pn.career.services.IFollowerService;
import com.pn.career.services.IResumeService;
import com.pn.career.services.IStudentSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/students")
@RequiredArgsConstructor
public class StudentController {
    private final IResumeService resumeService;
    private final IApplicationService applicationService;
    private final IStudentSkillService studentSkillService;
    private final IFollowerService followerService;
    @PostMapping("/upload/resumes")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> uploadResume(@AuthenticationPrincipal Jwt jwt, @ModelAttribute ResumeDTO resumeDTO) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        Resume resume= resumeService.createResume(studentId, resumeDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(ResumeResponse.fromResume(resume))
                .message("Upload cv thành công")
                .build());

    }
    @GetMapping("/resumes")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getAllResumes(@AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        List<Resume> resumes = resumeService.getResumesByStudentId(studentId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(resumes.stream().map(ResumeResponse::fromResume).toList())
                .message("Lấy danh sách cv thành công")
                .build());
    }
    @GetMapping("/resumes/{resumeId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getResumeById(@PathVariable Integer resumeId) {
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(ResumeResponse.fromResume(resumeService.getResumeById(resumeId)))
                .message("Lấy cv thành công")
                .build());
    }
    @PostMapping("/jobs/apply")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> applyJob(@RequestParam Integer jobId, @RequestParam Integer resumeId) {
        try{
            Application application=applicationService.createApplication(jobId, resumeId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(ApplicationResponse.fromApplication(application))
                    .message("Ứng tuyển thành công")
                    .build());
        }catch (Exception e){
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
    @PostMapping("/skills/add")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> addSkill(@AuthenticationPrincipal Jwt jwt, @RequestParam Integer skillId, @RequestParam Integer level) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        StudentSkill studentSkill=studentSkillService.createStudentSkill(studentId, skillId, level);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(StudentSkillResponse.fromStudentSkill(studentSkill))
                .message("Thêm kỹ năng thành công")
                .build());
    }
    //@PostMapping("/send-email/job-recommendations")

    @PostMapping("/follow")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> followEmployer(@RequestParam Integer employerId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        followerService.createFollower(studentId, employerId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Theo dõi nhà tuyển dụng thành công")
                .build());
    }
    @DeleteMapping("/unfollow")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> unfollowEmployer(@RequestParam Integer employerId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        followerService.unFollow(studentId, employerId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Bỏ theo dõi nhà tuyển dụng thành công")
                .build());
    }

}
