package com.pn.career.controllers;

import com.pn.career.models.EmployerStatus;
import com.pn.career.models.JobStatus;
import com.pn.career.responses.AboutStatisticalResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IEmployerService;
import com.pn.career.services.IEventService;
import com.pn.career.services.IJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("${api.prefix}/statistical")
@RequiredArgsConstructor
public class StatisticalController {
    private final IEventService eventService;
    private final IEmployerService employerService;
    private final IJobService jobService;
    @GetMapping("/about")
    public ResponseEntity<ResponseObject> getStatisticalAbout() {
        Integer year = LocalDateTime.now().getYear();
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Get statistical about successfully")
                .data(AboutStatisticalResponse.fromStatistical(
                        eventService.countEventsByYear(year),
                        employerService.countEmployerByStatus(EmployerStatus.APPROVED),
                        jobService.countJobByActiveStatus(JobStatus.ACTIVE)))
                .build());
    }

}
