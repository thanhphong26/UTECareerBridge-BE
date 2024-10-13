package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.models.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EventResponse {
    private String eventTitle;
    private String eventDescription;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime eventDate;
    private String eventLocation;
    private String eventImage;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    public static EventResponse from(Event event) {
        return EventResponse.builder()
                .eventTitle(event.getEventTitle())
                .eventDescription(event.getEventDescription())
                .eventDate(event.getEventDate())
                .eventLocation(event.getEventLocation())
                .eventImage(event.getEventImage())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
