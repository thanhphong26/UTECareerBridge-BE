package com.pn.career.controllers.forum;

import com.pn.career.models.Reaction;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.forum.IReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/reactions")
@RequiredArgsConstructor
public class ReactionController {
    private final IReactionService reactionService;
    @GetMapping
    public ResponseEntity<ResponseObject> getAllReactions() {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách phản ứng thành công")
                .status(HttpStatus.OK)
                .data(reactionService.getAllReactions())
                .build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getReactionById(@PathVariable  Integer reactionId) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy phản ứng thành công")
                .status(HttpStatus.OK)
                .data(reactionService.getReactionById(reactionId))
                .build());
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseObject> getReactionsByUserId(Integer userId) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách phản ứng theo người dùng thành công")
                .status(HttpStatus.OK)
                .data(reactionService.getReactionsByUserId(userId))
                .build());
    }
    @GetMapping("/post/{postId}")
    public ResponseEntity<ResponseObject> getReactionsByPostId(Integer postId) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách phản ứng theo bài viết thành công")
                .status(HttpStatus.OK)
                .data(reactionService.getReactionsByPostId(postId))
                .build());
    }
    @GetMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<ResponseObject> getReactionByPostIdAndUserId(Integer postId, Integer userId) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy phản ứng theo bài viết và người dùng thành công")
                .status(HttpStatus.OK)
                .data(reactionService.getUserReactionOnPost(postId, userId))
                .build());
    }
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<ResponseObject> getReactionCountByPostId(Integer postId) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy số lượng phản ứng theo bài viết thành công")
                .status(HttpStatus.OK)
                .data(reactionService.getReactionCountsByPostId(postId))
                .build());
    }
    @PostMapping
    public ResponseEntity<ResponseObject> createReaction(@RequestBody Reaction reaction) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tạo phản ứng thành công")
                .status(HttpStatus.CREATED)
                .data(reactionService.addOrUpdateReaction(reaction))
                .build());
    }
    @DeleteMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<ResponseObject> deleteReaction(@PathVariable Integer postId, @PathVariable Integer userId) {
        reactionService.removeUserReactionOnPost(postId, userId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Xóa phản ứng thành công")
                .status(HttpStatus.OK)
                .build());
    }

}
