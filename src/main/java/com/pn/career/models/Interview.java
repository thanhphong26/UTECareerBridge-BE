package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Table(name = "interviews", indexes = {
        @Index(name = "idx_interview_application_id", columnList = "application_id"),
        @Index(name = "idx_interview_employer_id", columnList = "employer_id")
})
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString(exclude = {"application"})
@EqualsAndHashCode(exclude = {"application"})
public class Interview extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interview_id")
    private int interviewId;
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "application_id")
    private Application application;
    @Column(name = "schedule_date")
    private LocalDateTime scheduleDate;
    @Column(name = "duration")
    private int duration;
    @Column(name = "meeting_link")
    private String meetingLink;
    @Column(name = "meeting_password")
    private String meetingPassword;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InterviewStatus status = InterviewStatus.SCHEDULED;
    private Integer employerId;
}
