package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.dtos.EventDTO;
import com.pn.career.dtos.EventTimelineDTO;
import com.pn.career.models.Event;
import com.pn.career.models.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EventResponse {
    private String eventTitle;
    private String eventDescription;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime eventDate;
    private String eventLocation;
    private String eventImage;
    private int maxParticipants;
    private int currentParticipants;
    private EventType eventType;
    private List<EventTimelineDTO> timeline;
    public static EventResponse from(Event event) {
        return EventResponse.builder()
                .eventTitle(event.getEventTitle())
                .eventDescription(event.getEventDescription())
                .eventDate(event.getEventDate())
                .eventLocation(event.getEventLocation())
                .eventImage(event.getEventImage())
                .maxParticipants(event.getMaxParticipants())
                .currentParticipants(event.getCurrentParticipants())
                .eventType(event.getEventType())
                .timeline(event.getEventTimelines().stream().map(timeline -> EventTimelineDTO.builder()
                        .timelineId(timeline.getTimelineId())
                        .timelineTitle(timeline.getTimelineTitle())
                        .timelineDescription(timeline.getTimelineDescription())
                        .timelineStart(timeline.getTimelineStart())
                        .build()).toList())
                .build();
    }
}
