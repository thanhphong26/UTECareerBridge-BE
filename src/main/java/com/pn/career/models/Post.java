package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "posts", indexes = {
        @jakarta.persistence.Index(name = "idx_posts_topic_id", columnList = "topic_id"),
        @jakarta.persistence.Index(name = "idx_posts_user_id", columnList = "user_id"),
})
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;
    @Column(name = "topic_id")
    private Integer topicId;
    @Column(name = "user_id")
    private Integer userId;
    private String content;
    @Column(name = "is_active")
    private boolean active;
}
