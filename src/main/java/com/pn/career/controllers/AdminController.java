package com.pn.career.controllers;

import com.pn.career.dtos.EventDTO;
import com.pn.career.models.Event;
import com.pn.career.models.EventType;
import com.pn.career.repositories.JobRepository;
import com.pn.career.responses.EventListResponse;
import com.pn.career.responses.EventResponse;
import com.pn.career.responses.RecentOrderStatResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin")
@RequiredArgsConstructor
public class AdminController {
    private final IEventService eventService;
    private final IAdminService adminService;
    private final IOrderService orderService;
    private final IJobService jobService;
    private final IUserService userService;
    private final IApplicationService applicationService;
    @GetMapping("/forum-statistics")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getForumStatistics(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thống kê diễn đàn thành công")
                .data(adminService.getForumStatsByDate(startDate, endDate))
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/application-statistics")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getApplicationStatistics(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thống kê đơn ứng tuyển thành công")
                .data(applicationService.getApplicationStatsByDate(startDate, endDate))
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/top-employers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getTopEmployers(@RequestParam(defaultValue = "10") int limit,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách nhà tuyển dụng hàng đầu thành công")
                .data(userService.getTopEmployers(limit, startDate, endDate))
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/top-requested-skills")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getTopRequestedSkills(@RequestParam(defaultValue = "10") int limit,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách kỹ năng được yêu cầu nhiều nhất thành công")
                .data(jobService.getTopRequestedSkills(limit, startDate, endDate))
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/users-statistics")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getUserStatistics(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thống kê người dùng thành công")
                .data(userService.getMonthlyUserGrowth(startDate, endDate))
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/jobs-statistics")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getJobStatistics(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thống kê job thành công")
                .data(jobService.getStatisticsJobByAdmin(startDate, endDate))
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/recent-orders")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getRecentOrders( @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                           @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int limit) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<RecentOrderStatResponse> orderStats = orderService.getRecentOrderStats(startDate, endDate, pageRequest);

        if (orderStats.isEmpty()) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Không có đơn hàng nào")
                    .data(null)
                    .build());
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy danh sách đơn hàng thành công")
                .data(orderStats.getContent())
                .build());
    }

    @PostMapping("/events")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createEvent(@RequestBody EventDTO eventDTO){
        EventResponse event=eventService.createEvent(eventDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Thêm mới sự kiện thành công")
                .status(HttpStatus.CREATED)
                .data(event)
                .build());
    }
    @PutMapping("/events/{eventId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateEvent(@PathVariable Integer eventId,@RequestBody EventDTO eventDTO){
        EventResponse event=eventService.updateEvent(eventId,eventDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Cập nhật sự kiện thành công")
                .status(HttpStatus.OK)
                .data(event)
                .build());
    }
    @DeleteMapping("/events/{eventId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteEvent(@PathVariable Integer eventId){
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Xóa sự kiện thành công")
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/events/{eventId}")
    public ResponseEntity<ResponseObject> getEvent(@PathVariable Integer eventId){
        EventResponse eventResponse=eventService.getEventById(eventId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .data(eventResponse)
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/events/get-all")
    public ResponseEntity<ResponseObject> getAllEvents(@RequestParam(required = false) EventType eventType, @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size){
        PageRequest pageRequest=PageRequest.of(page,size);
        Page<Event> events=eventService.getAllEvents(eventType, pageRequest);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách sự kiện thành công")
                .data(EventListResponse.builder()
                        .eventResponses(events.map(EventResponse::from).getContent())
                        .totalPages(events.getTotalPages())
                        .build())
                .status(HttpStatus.OK)
                .build());
    }
    //get sự kiện sắp diễn ra
    @GetMapping("/events/upcoming")
    public ResponseEntity<ResponseObject> getUpcomingEvents(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size){
        LocalDateTime dateNow = LocalDateTime.now();
        List<EventResponse> events=eventService.getAllEventUpcomming(dateNow, size);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách sự kiện sắp diễn ra thành công")
                .data(EventListResponse.builder()
                        .eventResponses(events)
                        .build())
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/statistics/category-job")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getJobCategoryStats(@RequestParam(required = false) Integer month,
                                                               @RequestParam(required = false) Integer year){
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thống kê số lượng job theo category thành công")
                .data(adminService.getJobCategoryStats(month,year))
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/statistics/revenue-by-month")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getRevenueByMonth(@RequestParam(required = false) Integer year){
        if(year==null){
            year = LocalDate.now().getYear();
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thống kê doanh thu theo tháng thành công")
                .data(adminService.getRevenueByMonth(year))
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/statistics-user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getUserStats(){
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thống kê người dùng thành công")
                .data(adminService.getUserStats())
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/statistics-package")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getPackageBestSeller(){
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thống kê gói tuyển dụng bán chạy nhất thành công")
                .data(adminService.getPackageBestSeller())
                .status(HttpStatus.OK)
                .build());
    }
}
