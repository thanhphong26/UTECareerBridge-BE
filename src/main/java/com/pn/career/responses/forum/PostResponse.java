package com.pn.career.responses.forum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private Integer postId;
    private Integer topicId;
    private Integer userId;
    private String userName;
    private String avatar;
    private String roleName;
    private String content;
    private boolean active;
    private String createdAt;
    private String updatedAt;

    public static PostResponse fromPost(com.pn.career.models.Post post) {
        return PostResponse.builder()
                .postId(post.getPostId())
                .topicId(post.getTopicId())
                .userId(post.getUserId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt().toString())
                .updatedAt(post.getUpdatedAt().toString())
                .active(post.isActive())
                .build();
    }
}
