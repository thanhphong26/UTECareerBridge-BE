package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "followers")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Follower {
    @EmbeddedId
    private FollowerId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employerId")
    @JoinColumn(name = "employer_id")
    private Employer employer;
}
