package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table(name = "interviews")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Interview extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interview_id")
    private int interviewId;
    @OneToOne
    @JoinColumn(name = "application_id")
    private Application application;
    @Column(name = "schedule_date")
    private String scheduleDate;
    @Column(name = "duration")
    private int duration;
    @Column(name = "meeting_link")
    private String meetingLink;
    @Column(name = "meeting_password")
    private String meetingPassword;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InterviewStatus status = InterviewStatus.SCHEDULED;
}
