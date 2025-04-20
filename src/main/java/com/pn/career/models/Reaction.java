package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reactions", indexes = {
        @Index(name = "idx_reactions_post_id", columnList = "post_id"),
        @Index(name = "idx_reactions_user_id", columnList = "user_id"),
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reaction {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name ="reaction_id")
    private Integer reactionId;
    @Column(name = "post_id")
    private Integer postId;
    @Column(name = "user_id")
    private Integer userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type")
    private ReactionType type;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
