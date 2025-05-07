package com.pn.career.responses.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionCountResponse {
    private Integer postId;
    private Long likeCount;
    private Long dislikeCount;
    private Long loveCount;
    private Long hahaCount;
    private Long wowCount;
    private Long sadCount;
    private Long angryCount;
    private Long totalCount;
}
