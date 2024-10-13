package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EventDTO {
    private String eventTitle;
    private String eventDescription;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime eventDate;
    private String eventLocation;
    private MultipartFile eventImage;
}
