package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table(name = "forums", indexes = {
        @Index(name = "idx_forums_is_active", columnList = "is_active"),
})
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Forum extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forum_id")
    private Integer forumId;
    private String name;
    private String description;
    private String image;
    @Column(name = "is_active")
    private boolean isActive;
}
