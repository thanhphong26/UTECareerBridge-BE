package com.pn.career.services;

import com.pn.career.dtos.EventDTO;
import com.pn.career.models.Event;
import com.pn.career.models.EventType;
import com.pn.career.responses.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface IEventService {
    Page<Event> getAllEvents(EventType eventType, PageRequest pageRequest);
    EventResponse createEvent(EventDTO eventDTO);
    EventResponse updateEvent(Integer eventId, EventDTO eventDTO);
    void deleteEvent(Integer eventId);
    EventResponse getEventById(Integer eventId);
    Integer countEventUpcomming(LocalDateTime dateNow);
    List<EventResponse> getAllEventUpcomming(LocalDateTime dateNow, Integer limit);
}
