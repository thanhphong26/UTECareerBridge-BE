package com.pn.career.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Table(name = "events")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Event extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private int eventId;
    @Column(name = "event_title")
    private String eventTitle;
    @Column(name = "event_description")
    private String eventDescription;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Column(name = "event_location")
    private String eventLocation;
    @Column(name = "event_image")
    private String eventImage;
    @Column(name = "max_participants")
    private int maxParticipants;
    @Column(name = "current_participants")
    private int currentParticipants;
    @Column(name = "event_type")
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private List<EventTimeline> eventTimelines;
}
