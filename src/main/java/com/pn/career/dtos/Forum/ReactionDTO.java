package com.pn.career.dtos.Forum;

import com.pn.career.models.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReactionDTO {
    private Integer postId;
    private Integer userId;
    private ReactionType reactionType;
}
