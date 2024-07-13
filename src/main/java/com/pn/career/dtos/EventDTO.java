package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventDTO {
    @NotNull
    @Size(max = 100)
    @JsonProperty("event_title")
    private String eventTitle;

    @JsonProperty("event_description")
    private String eventDescription;

    @JsonProperty("event_date")
    private LocalDateTime eventDate;

    @Size(max = 100)
    @JsonProperty("event_location")
    private String eventLocation;

    @Size(max = 255)
    @JsonProperty("event_image")
    private String eventImage;
}
