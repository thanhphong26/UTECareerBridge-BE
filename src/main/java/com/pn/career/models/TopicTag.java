package com.pn.career.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "topic_tags")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicTag {
    @EmbeddedId
    private TopicTagId id;
    @ManyToOne
    @JoinColumn(name = "topic_id", insertable = false, updatable = false)
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "tag_id", insertable = false, updatable = false)
    private Tag tag;
}
