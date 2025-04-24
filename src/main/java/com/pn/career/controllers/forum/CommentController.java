package com.pn.career.controllers.forum;

import com.nimbusds.jwt.JWT;
import com.pn.career.dtos.Forum.CommentDTO;
import com.pn.career.models.Comment;
import com.pn.career.responses.ResponseObject;
import com.pn.career.responses.forum.CommentResponse;
import com.pn.career.services.forum.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAllComments(@RequestParam(defaultValue = "0") Integer page,
                                                         @RequestParam(defaultValue = "1") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách bình luận thành công")
                .status(HttpStatus.OK)
                .data(commentService.getAllComments(pageRequest))
                .build());
    }
    @GetMapping("/{commentId}")
    public ResponseEntity<ResponseObject> getCommentById(@PathVariable Integer commentId) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy bình luận thành công")
                .status(HttpStatus.OK)
                .data(commentService.getCommentById(commentId))
                .build());
    }
    @GetMapping("/post/{postId}")
    public ResponseEntity<ResponseObject> getCommentsByPostId(@PathVariable  Integer postId,
                                                               @RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "1") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách bình luận theo bài viết thành công")
                .status(HttpStatus.OK)
                .data(commentService.getCommentsByPostId(postId, pageRequest))
                .build());
    }
    @GetMapping("/post/{postId}/root")
    public ResponseEntity<ResponseObject> getRootCommentsByPostId(
            @PathVariable Integer postId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách bình luận gốc theo bài viết thành công")
                .status(HttpStatus.OK)
                .data(commentService.getRootCommentsByPostId(postId, pageRequest))
                .build());
    }
    @GetMapping("/parent/{parentId}/child")
    public ResponseEntity<ResponseObject> getCommentsByParentId(@PathVariable Integer parentId,
                                                                  @RequestParam(defaultValue = "0") Integer page,
                                                                  @RequestParam(defaultValue = "5") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CommentResponse> commentResponses = commentService.getChildComments(parentId, pageRequest);

        boolean hasMoreReplies = commentResponses.hasNext();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("comments", commentResponses.getContent());
        responseData.put("hasMoreReplies", hasMoreReplies);
        responseData.put("totalElements", commentResponses.getTotalElements());
        responseData.put("totalPages", commentResponses.getTotalPages());
        responseData.put("currentPage", commentResponses.getNumber());

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách bình luận theo bình luận thành công")
                .status(HttpStatus.OK)
                .data(responseData)
                .build());
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> createComment(@RequestBody CommentDTO comment, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Thêm bình luận thành công")
                .status(HttpStatus.OK)
                .data(commentService.createComment(comment, userId))
                .build());
    }
    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseObject> updateComment(@PathVariable Integer commentId, @RequestBody CommentDTO comment, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Cập nhật bình luận thành công")
                .status(HttpStatus.OK)
                .data(commentService.updateComment(commentId, comment, userId))
                .build());
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseObject> deleteComment(@PathVariable Integer commentId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Xóa bình luận thành công")
                .status(HttpStatus.OK)
                .build());
    }
}
