package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "topics", indexes = {
        @jakarta.persistence.Index(name = "idx_topics_forum_id", columnList = "forum_id"),
        @jakarta.persistence.Index(name = "idx_topics_user_id", columnList = "user_id"),
        @jakarta.persistence.Index(name = "idx_topics_status", columnList = "status"),
        @jakarta.persistence.Index(name = "idx_posts_topic_id", columnList = "topic_id"),
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Topic extends BaseEntity{
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Integer topicId;
    @Column(name = "forum_id")
    private Integer forumId;
    @Column(name = "user_id")
    private Integer userId;
    private String title;
    private String content;
    @Column(name = "view_count")
    private Integer viewCount;
    @Column(name = "is_pinned")
    private boolean isPinned;
    @Column(name = "is_closed")
    private boolean isClose;
    //ENUM
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TopicStatus status;
    @Column(name = "post_count")
    private Integer postCount;
}
