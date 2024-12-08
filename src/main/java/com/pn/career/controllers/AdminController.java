package com.pn.career.controllers;

import com.pn.career.dtos.EventDTO;
import com.pn.career.models.Event;
import com.pn.career.models.EventType;
import com.pn.career.responses.EventListResponse;
import com.pn.career.responses.EventResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IAdminService;
import com.pn.career.services.IEventService;
import com.pn.career.services.IJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("${api.prefix}/admin")
@RequiredArgsConstructor
public class AdminController {
    private final IEventService eventService;
    private final IAdminService adminService;
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
