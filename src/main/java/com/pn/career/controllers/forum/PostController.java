package com.pn.career.controllers.forum;

import com.google.api.Page;
import com.pn.career.dtos.Forum.PostDTO;
import com.pn.career.models.Post;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.forum.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/posts")
public class PostController {
    private final IPostService postService;
    @GetMapping
    public ResponseEntity<ResponseObject> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách bài viết thành công")
                .status(HttpStatus.OK)
                .data(postService.getAllPosts(pageRequest))
                .build());
    }
    @GetMapping("/{postId}")
    public ResponseEntity<ResponseObject> getPostById(@PathVariable Integer postId) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy bài viết thành công")
                .status(HttpStatus.OK)
                .data(postService.getPostById(postId))
                .build());
    }
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<ResponseObject> getPostsByTopicId(
            @PathVariable Integer topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách bài viết theo chủ đề thành công")
                .status(HttpStatus.OK)
                .data(postService.getPostsByTopicId(topicId, pageable))
                .build());
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseObject> getPostsByUserId(@PathVariable Integer userId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách bài viết theo người dùng thành công")
                .status(HttpStatus.OK)
                .data(postService.getPostsByUserId(userId, pageable))
                .build());
    }
    @PostMapping
    public ResponseEntity<ResponseObject> createPost(@RequestBody PostDTO post) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tạo bài viết thành công")
                .status(HttpStatus.CREATED)
                .data(postService.createPost(post))
                .build());
    }
    @PutMapping("/{postId}")
    public ResponseEntity<ResponseObject> updatePost(@PathVariable Integer postId, @RequestBody PostDTO post, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Cập nhật bài viết thành công")
                .status(HttpStatus.OK)
                .data(postService.updatePost(postId, post, userId))
                .build());
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseObject> deletePost(@PathVariable Integer postId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        postService.deletePost(postId, userId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Xóa bài viết thành công")
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchPosts(@RequestParam String keyword,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tìm kiếm bài viết thành công")
                .status(HttpStatus.OK)
                .data(postService.searchPosts(keyword, pageRequest))
                .build());
    }
}
