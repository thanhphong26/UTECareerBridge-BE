package com.pn.career.dtos.Forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicTagDTO {
    private Integer forumId;
    private String title;
    private String content;
    private List<Integer> tags;
}
