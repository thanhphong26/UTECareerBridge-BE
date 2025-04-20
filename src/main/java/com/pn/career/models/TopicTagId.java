package com.pn.career.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TopicTagId implements Serializable {
    @Column(name = "topic_id")
    private Integer topicId;

    @Column(name = "tag_id")
    private Integer tagId;
}
