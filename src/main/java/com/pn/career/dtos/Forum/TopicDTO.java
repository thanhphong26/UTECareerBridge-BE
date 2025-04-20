package com.pn.career.dtos.Forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicDTO {
    private Integer forumId;
    private Integer userId;
    private String title;
    private String content;
    private Integer viewCount;
    private boolean isPined;
}
