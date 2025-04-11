package com.pn.career.controllers;

import com.pn.career.dtos.ResumeDTO;
import com.pn.career.dtos.StudentDTO;
import com.pn.career.event.JobAppliedEvent;
import com.pn.career.event.JobSavedEvent;
import com.pn.career.event.JobUnsavedEvent;
import com.pn.career.models.Application;
import com.pn.career.models.Resume;
import com.pn.career.models.StudentSkill;
import com.pn.career.responses.*;
import com.pn.career.services.*;
import com.pn.career.utils.JWTCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final ISaveJobService saveJobService;
    private final ApplicationEventPublisher applicationEventPublisher;
    @GetMapping("/students-finding-job")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getStudentIsFindingJob(@RequestParam(required = false) Integer categoryId, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        PageRequest pageRequest=PageRequest.of(page,limit);
        Page<StudentViewResponse> studentViewResponses=studentService.getStudentIsFindingJob(categoryId, pageRequest);
        if(studentViewResponses.isEmpty()){
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .message("Không có sinh viên nào đang tìm việc")
                    .build());
        }
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(studentViewResponses)
                .message("Lấy danh sách sinh viên đang tìm việc thành công")
                .build());
    }
    @GetMapping("/follow/company")
    public ResponseEntity<ResponseObject> checkFollowCompany(@RequestParam Integer companyId, @AuthenticationPrincipal Jwt jwt) {
        if(jwt==null){
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(null)
                    .message("Kiểm tra theo dõi nhà tuyển dụng thành công")
                    .build());
        }
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        boolean isFollowed = followerService.isFollowing(studentId, companyId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(isFollowed)
                .message("Kiểm tra theo dõi nhà tuyển dụng thành công")
                .build());
    }
    @GetMapping("/jobs/check")
    public ResponseEntity<ResponseObject> checkJobSaved(@RequestParam Integer jobId, @AuthenticationPrincipal Jwt jwt) {
        if(jwt==null){
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(null)
                    .message("Kiểm tra công việc đã lưu thành công")
                    .build());
        }
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        boolean isSaved = saveJobService.isSaved(studentId, jobId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(isSaved)
                .message("Kiểm tra công việc đã lưu thành công")
                .build());
    }
    @PostMapping("/jobs/saved/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> saveJob(@PathVariable Integer jobId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        saveJobService.saveJob(studentId, jobId);
        applicationEventPublisher.publishEvent(new JobSavedEvent(studentId, jobId));
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lưu công việc thành công")
                .build());
    }
    @DeleteMapping("/jobs/unsaved/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> unsaveJob(@PathVariable Integer jobId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        saveJobService.unsaveJob(studentId, jobId);
        applicationEventPublisher.publishEvent(new JobUnsavedEvent(studentId, jobId));
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Hủy lưu công việc thành công")
                .build());
    }
    @GetMapping("/jobs/saved")
    public ResponseEntity<ResponseObject> getAllJobSaved(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        PageRequest pageRequest=PageRequest.of(page,limit);
        Page<JobResponse> jobResponses=saveJobService.getSavedJobs(studentId, pageRequest);
        if(jobResponses.isEmpty()){
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .message("Bạn chưa lưu công việc nào. Vui lòng lưu công việc để xem danh sách công việc đã lưu")
                    .build());
        }
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(jobResponses)
                .message("Lấy danh sách công việc đã lưu thành công")
                .build());
    }

    @GetMapping("/jobs")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getJobApplyByStudentId(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit ) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        PageRequest pageRequest=PageRequest.of(page,limit);
        Page<ApplicationResponse> jobResponses=studentService.getJobApplyByStudentId(studentId, pageRequest);
        if(jobResponses.isEmpty()){
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .message("Bạn chưa ứng tuyển công việc nào. Vui lòng ứng tuyển công việc để xem danh sách công việc đã ứng tuyển")
                    .build());
        }
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(jobResponses)
                .message("Lấy danh sách công việc đã ứng tuyển thành công")
                .build());
    }
    @GetMapping("/resumes/{resumeId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getResumeByStudentId(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer resumeId) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        Resume resume = resumeService.getResumeById(studentId, resumeId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(StudentApplicationResponse.fromStudent(resume))
                .message("Lấy hồ sơ thành công")
                .build());
    }
    @DeleteMapping("/resume")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> deleteResume(@RequestParam Integer resumeId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        resumeService.deleteResume(studentId, resumeId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Xóa cv thành công")
                .build());
    }
    @PutMapping("/resume/{resumeId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> updateResume(@PathVariable Integer resumeId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        resumeService.updateActiveResume(resumeId, studentId, true);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(ResumeResponse.fromResume(resumeService.getResumeById(studentId,resumeId)))
                .message("Cập nhật cv thành công")
                .build());
    }
    @PutMapping("/is-finding-job")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> updateIsFindingJob(@AuthenticationPrincipal Jwt jwt, @RequestParam boolean isFindingJob) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        studentService.updateIsFindingJob(studentId, isFindingJob);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Bật tìm việc thành công")
                .build());
    }
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
    @PostMapping("/jobs/apply")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> applyJob(@RequestParam Integer jobId, @RequestParam Integer resumeId, @AuthenticationPrincipal Jwt jwt) {
        try{
            Application application=applicationService.createApplication(jobId, resumeId);
            if(JWTCheck.getUserIdFromJWT(jwt)!=null){
                applicationEventPublisher.publishEvent(new JobAppliedEvent(JWTCheck.getUserIdFromJWT(jwt), jobId));
            }
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

    @PostMapping("/follow/{employerId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> followEmployer(@PathVariable Integer employerId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        followerService.createFollower(studentId, employerId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Theo dõi nhà tuyển dụng thành công")
                .build());
    }
    @DeleteMapping("/unfollow/{employerId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> unfollowEmployer(@PathVariable Integer employerId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        followerService.unFollow(studentId, employerId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Bỏ theo dõi nhà tuyển dụng thành công")
                .build());
    }
    @GetMapping("/get-all-followed-employers")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getAllFollowedEmployers(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        Long userIdLong = jwt.getClaim("userId");
        Integer studentId = userIdLong != null ? userIdLong.intValue() : null;
        PageRequest pageRequest=PageRequest.of(page,limit);
        Page<EmployerResponse> employerResponses=followerService.getFollowedEmployers(studentId, pageRequest);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(employerResponses)
                .message("Lấy danh sách nhà tuyển dụng theo dõi thành công")
                .build());
    }
}
