package com.pn.career.controllers.forum;

import com.pn.career.dtos.Forum.TagDTO;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.forum.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    @GetMapping
    public ResponseEntity<ResponseObject> getAllTags(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách thẻ thành công")
                .status(HttpStatus.OK)
                .data(tagService.getAllTags(pageRequest))
                .build());
    }
    @GetMapping("/{tagId}")
    public ResponseEntity<ResponseObject> getTagById(@PathVariable Integer tagId) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy thẻ thành công")
                .status(HttpStatus.OK)
                .data(tagService.getTagById(tagId))
                .build());
    }
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<ResponseObject> getTagsByTopicId(@PathVariable Integer topicId,
                                                           @RequestParam(defaultValue = "0") Integer page,
                                                           @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách thẻ theo chủ đề thành công")
                .status(HttpStatus.OK)
                .data(tagService.getTagsByTopicId(topicId, PageRequest.of(page, size)))
                .build());
    }
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> createTag(@RequestBody TagDTO tag) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tạo thẻ thành công")
                .status(HttpStatus.OK)
                .data(tagService.createTag(tag))
                .build());
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> updateTag(@PathVariable Integer id, @RequestBody TagDTO tag) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Cập nhật thẻ thành công")
                .status(HttpStatus.OK)
                .data(tagService.updateTag(id, tag))
                .build());
    }
    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchTags(@RequestParam String keyword,
                                                     @RequestParam(defaultValue = "0") Integer page,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tìm kiếm thẻ thành công")
                .status(HttpStatus.OK)
                .data(tagService.searchTags(keyword, PageRequest.of(page, size)))
                .build());
    }
}
