package com.pn.career.responses.forum;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.models.ReactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReactionResponse {
    private Integer reactionId;
    private Integer postId;
    private Integer userId;
    private String userName;
    private String avatar;
    private String roleName;
    private ReactionType type;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private ReactionCountResponse reactionCount;
    public static ReactionResponse fromReaction(com.pn.career.models.Reaction reaction) {
        return ReactionResponse.builder()
                .reactionId(reaction.getReactionId())
                .postId(reaction.getPostId())
                .userId(reaction.getUserId())
                .type(ReactionType.valueOf(reaction.getType().name()))
                .createdAt(reaction.getCreatedAt())
                .build();
    }
}
