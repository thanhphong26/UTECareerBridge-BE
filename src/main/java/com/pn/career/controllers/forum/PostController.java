package com.pn.career.controllers.forum;

import com.nimbusds.jose.jwk.source.JWKSourceWithFailover;
import com.pn.career.dtos.Forum.PostDTO;
import com.pn.career.dtos.Forum.ReactionDTO;
import com.pn.career.responses.ResponseObject;
import com.pn.career.responses.forum.ReactionCountResponse;
import com.pn.career.responses.forum.ReactionResponse;
import com.pn.career.services.forum.IPostService;
import com.pn.career.services.forum.IReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/posts")
public class PostController {
    private final IPostService postService;
    private final IReactionService reactionService;
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> createPost(@RequestBody PostDTO post) {
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tạo bài viết thành công")
                .status(HttpStatus.CREATED)
                .data(postService.createPost(post))
                .build());
    }
    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYER') or hasRole('ROLE_STUDENT')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYER') or hasRole('ROLE_STUDENT')")
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
    //Reactions
    @PostMapping("/{postId}/reactions")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> addReaction(
            @PathVariable Integer postId,
            @RequestBody ReactionDTO request, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        ReactionResponse reaction = reactionService.createReaction(request, userId, postId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Thêm cảm xúc thành công")
                .status(HttpStatus.OK)
                .data(reaction)
                .build());
    }

    @DeleteMapping("/{postId}/reactions")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> removeReaction(
            @PathVariable Integer postId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        reactionService.removeReaction(postId, userId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Xóa cảm xúc thành công")
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/{postId}/reactions/me")
    public ResponseEntity<ResponseObject> getUserReaction(
            @PathVariable Integer postId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        ReactionResponse reaction = reactionService.getUserReaction(postId, userId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy cảm xúc của người dùng thành công")
                .status(HttpStatus.OK)
                .data(reaction)
                .build());
    }

    @GetMapping("/{postId}/reactions")
    public ResponseEntity<ResponseObject> getReactions(@PathVariable Integer postId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ReactionResponse> reactions = reactionService.getReactionsByPostId(postId, pageRequest);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách cảm xúc thành công")
                .status(HttpStatus.OK)
                .data(reactions)
                .build());
    }
    @GetMapping("/{postId}/reactions/count")
    public ResponseEntity<ResponseObject> getReactionCounts(@PathVariable Integer postId) {
        ReactionCountResponse reactionCount = reactionService.getReactionCountByPostId(postId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy số lượng cảm xúc thành công")
                .status(HttpStatus.OK)
                .data(reactionCount)
                .build());
    }
}
