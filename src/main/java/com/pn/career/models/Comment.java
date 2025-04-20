package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments", indexes = {
        @jakarta.persistence.Index(name = "idx_comments_post_id", columnList = "post_id"),
        @jakarta.persistence.Index(name = "idx_comments_parent_comment_id", columnList = "parent_comment_id"),
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "comment_id")  // Explicitly name the column
    private Integer commentId;
    @Column(name = "post_id")
    private Integer postId;
    @Column(name = "user_id")
    private Integer userId;
    private String content;
    @Column(name = "parent_comment_id")
    private Integer parentCommentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", referencedColumnName = "comment_id", insertable = false, updatable = false)
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<Comment> childComments = new ArrayList<>();

}
