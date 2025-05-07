package com.pn.career.responses.forum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicResponse {
    private Integer topicId;
    private Integer forumId;
    private Integer userId;
    private String userName;
    private String avatar;
    private String roleName;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer postCount;
    private boolean isPinned;
    private boolean isClose;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    private List<TagResponse> tags;
    public static TopicResponse fromTopic(com.pn.career.models.Topic topic) {
        return TopicResponse.builder()
                .topicId(topic.getTopicId())
                .forumId(topic.getForumId())
                .userId(topic.getUserId())
                .title(topic.getTitle())
                .content(topic.getContent())
                .viewCount(topic.getViewCount())
                .postCount(topic.getPostCount())
                .isPinned(topic.isPinned())
                .isClose(topic.isClose())
                .status(topic.getStatus().toString())
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())
                .build();
    }
}
