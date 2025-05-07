package com.pn.career.controllers.forum;

import com.pn.career.dtos.Forum.ForumDTO;
import com.pn.career.models.Forum;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.forum.IForumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/forums")
@RequiredArgsConstructor
public class ForumController {
    private final IForumService forumService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAllForums(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Forum> forums = forumService.getAllForums(pageRequest);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách forum thành công")
                .status(HttpStatus.OK)
                .data(forums)
                .build());
    }
    @GetMapping("/active")
    public ResponseEntity<ResponseObject> getActiveForums(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Forum> forums = forumService.getActiveForums(pageRequest);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách forum thành công")
                .status(HttpStatus.OK)
                .data(forums)
                .build());
    }
    @GetMapping("/{forumId}")
    public ResponseEntity<ResponseObject> getForumById(@PathVariable Integer forumId){
        Optional<Forum> forum = forumService.getForumById(forumId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy forum thành công")
                .status(HttpStatus.OK)
                .data(forum.get())
                .build());
    }
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createForum(@RequestBody ForumDTO forum){
        Forum createdForum = forumService.createForum(forum);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tạo forum thành công")
                .status(HttpStatus.OK)
                .data(createdForum)
                .build());
    }
    @PutMapping("/{forumId}")
    public ResponseEntity<ResponseObject> updateForum(@PathVariable Integer forumId, @RequestBody ForumDTO forum){
        Forum updatedForum = forumService.updateForum(forumId, forum);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Cập nhật forum thành công")
                .status(HttpStatus.OK)
                .data(updatedForum)
                .build());
    }
    @DeleteMapping("/{forumId}")
    public ResponseEntity<ResponseObject> deleteForum(@PathVariable Integer forumId){
        forumService.deleteForum(forumId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Xóa forum thành công")
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchForums(@RequestParam String keyword, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Forum> forums = forumService.searchForums(keyword, pageRequest);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tìm kiếm forum thành công")
                .status(HttpStatus.OK)
                .data(forums)
                .build());
    }
}
