package com.pn.career.controllers;

import com.pn.career.dtos.EventDTO;
import com.pn.career.models.Event;
import com.pn.career.responses.EventListResponse;
import com.pn.career.responses.EventResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/admin")
@RequiredArgsConstructor
public class AdminController {
    private final IEventService eventService;
    @PostMapping("/events")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createEvent(@ModelAttribute EventDTO eventDTO){
        Event event=eventService.createEvent(eventDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Thêm mới sự kiện thành công")
                .status(HttpStatus.CREATED)
                .data(EventResponse.from(event))
                .build());
    }
    @PutMapping("/events/{eventId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateEvent(@PathVariable Integer eventId,@ModelAttribute EventDTO eventDTO){
        Event event=eventService.updateEvent(eventId,eventDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Cập nhật sự kiện thành công")
                .status(HttpStatus.OK)
                .data(EventResponse.from(event))
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
        Event event=eventService.getEvent(eventId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .data(EventResponse.from(event))
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/events/get-all")
    public ResponseEntity<ResponseObject> getAllEvents(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size){
        PageRequest pageRequest=PageRequest.of(page,size);
        Page<Event> events=eventService.getAllEvents(pageRequest);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách sự kiện thành công")
                .data(EventListResponse.builder()
                        .eventResponses(events.map(EventResponse::from).getContent())
                        .totalPages(events.getTotalPages())
                        .build())
                .status(HttpStatus.OK)
                .build());
    }
}
