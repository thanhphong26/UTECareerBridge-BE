package com.pn.career.responses.forum;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.models.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    private Integer commentId;
    private Integer postId;
    private Integer userId;
    private String userName;
    private String avatar;
    private String roleName;
    private String content;
    private Integer parentCommentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    private Integer replyCount;
    private boolean hasMoreReplies;
    public static CommentResponse fromComment(Comment comment){
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
