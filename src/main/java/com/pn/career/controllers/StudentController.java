package com.pn.career.controllers;

import com.pn.career.dtos.ResumeDTO;
import com.pn.career.dtos.StudentDTO;
import com.pn.career.models.Application;
import com.pn.career.models.Resume;
import com.pn.career.models.StudentSkill;
import com.pn.career.responses.*;
import com.pn.career.services.*;
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
    private final IStudentService studentService;
    private final IResumeService resumeService;
    private final IApplicationService applicationService;
    private final IStudentSkillService studentSkillService;
    private final IFollowerService followerService;
    @GetMapping("/infor")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getStudentInfor(@AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        StudentResponse studentResponse=studentService.getStudentById(studentId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(studentResponse)
                .message("Lấy thông tin sinh viên thành công")
                .build());
    }
    @PutMapping("/update-infor")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> updateStudent(@AuthenticationPrincipal Jwt jwt, @RequestBody StudentDTO studentDTO) {
        try{
            Long userIdLong = jwt.getClaim("userId");
            Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
            StudentResponse studentResponse=studentService.updateStudent(studentId, studentDTO);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(studentResponse)
                    .message("Cập nhật thông tin sinh viên thành công")
                    .build());
        }catch(Exception e){
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
    @PostMapping("/upload/resumes")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> uploadResume(@AuthenticationPrincipal Jwt jwt, @RequestBody ResumeDTO resumeDTO) {
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
    @GetMapping("/skills")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getSkills(@AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        List<StudentSkill> studentSkills=studentSkillService.getStudentSkills(studentId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(studentSkills.stream().map(StudentSkillResponse::fromStudentSkill).toList())
                .message("Lấy danh sách kỹ năng thành công")
                .build());
    }
    @DeleteMapping("/skills/delete")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> deleteSkill(@AuthenticationPrincipal Jwt jwt, @RequestParam Integer skillId) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        studentSkillService.deleteStudentSkill(studentId, skillId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Xóa kỹ năng thành công")
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
