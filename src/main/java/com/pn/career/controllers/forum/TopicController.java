package com.pn.career.controllers.forum;

import com.pn.career.dtos.Forum.TopicTagDTO;
import com.pn.career.responses.ResponseObject;
import com.pn.career.responses.forum.TopicResponse;
import com.pn.career.services.forum.ITopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/topics")
@RequiredArgsConstructor
@Slf4j
public class TopicController {
    private final ITopicService topicService;
    @GetMapping
    public ResponseEntity<ResponseObject> getAllTopics(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<TopicResponse> topics = topicService.getAllTopics(pageRequest);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách chủ đề thành công")
                .status(HttpStatus.OK)
                .data(topics)
                .build());
    }
   @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getTopicById(@PathVariable Integer id) {
        Optional<TopicResponse> topic = topicService.getTopicById(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy chủ đề thành công")
                .status(HttpStatus.OK)
                .data(topic)
                .build());
    }
    @GetMapping("/forum/{forumId}")
    public ResponseEntity<ResponseObject> getTopicsByForumId(
            @PathVariable Integer forumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<TopicResponse> topics = topicService.getTopicsByForumId(forumId, pageable);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách chủ đề theo forum thành công")
                .status(HttpStatus.OK)
                .data(topics)
                .build());

    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseObject> getTopicsByUserId(@PathVariable Integer userId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<TopicResponse> topics = topicService.getTopicsByUserId(userId, pageable);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách chủ đề theo người dùng thành công")
                .status(HttpStatus.OK)
                .data(topics)
                .build());
    }
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> createTopic(@RequestBody TopicTagDTO topic) {
        TopicResponse createdTopic = topicService.createTopic(topic);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tạo chủ đề thành công")
                .status(HttpStatus.OK)
                .data(createdTopic)
                .build());
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> updateTopic(@PathVariable Integer id, @RequestBody TopicTagDTO topic) {
        TopicResponse updatedTopic = topicService.updateTopic(id, topic);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Cập nhật chủ đề thành công")
                .status(HttpStatus.OK)
                .data(updatedTopic)
                .build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteTopic(@PathVariable Integer id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Xóa chủ đề thành công")
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchTopics(@RequestParam String keyword, @RequestParam(defaultValue = "createdAtDesc") String sortBy,
                                                       @RequestParam(required = false) List<Integer> tagIds,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size
                                                       ) {
        List<String> allowedSortFields = List.of("createdAtAsc", "createdAtDesc", "updatedAtAsc", "updatedAtDesc");
        if (!allowedSortFields.contains(sortBy) && sortBy != null) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Trường sắp xếp không hợp lệ")
                            .status(HttpStatus.BAD_REQUEST)
                            .data(null)
                            .build());
        }
        String sortField;
        Sort.Direction direction;

        switch (sortBy) {
            case "createdAtAsc":
                sortField = "createdAt";
                direction = Sort.Direction.ASC;
                break;
            case "createdAtDesc":
                sortField = "createdAt";
                direction = Sort.Direction.DESC;
                break;
            case "updatedAtAsc":
                sortField = "updatedAt";
                direction = Sort.Direction.ASC;
                break;
            case "updatedAtDesc":
                sortField = "updatedAt";
                direction = Sort.Direction.DESC;
                break;
            default:
                sortField = "createdAt";
                direction = Sort.Direction.DESC;
        }

        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Trang hoặc kích thước không hợp lệ")
                            .status(HttpStatus.BAD_REQUEST)
                            .data(null)
                            .build());
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortField));
        String normalizedKeyword = (keyword != null) ? keyword.trim() : null;

        log.info("Searching topics with keyword: {}, tagIds: {}, page: {}, size: {}",
                normalizedKeyword, tagIds, page, size);

        Page<TopicResponse> topics = topicService.searchTopics(normalizedKeyword, tagIds, pageRequest);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tìm kiếm chủ đề thành công")
                .status(HttpStatus.OK)
                .data(topics)
                .build());
    }
    @PostMapping("/{topicId}/pin")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> pinTopic(@PathVariable Integer topicId) {
        topicService.pinTopic(topicId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Ghim chủ đề thành công")
                .status(HttpStatus.OK)
                .build());
    }
}
