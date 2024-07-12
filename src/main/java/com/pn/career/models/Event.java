package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "events")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
}
