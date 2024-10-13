package com.pn.career.services;

import com.pn.career.dtos.EventDTO;
import com.pn.career.models.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IEventService {
    Event createEvent(EventDTO eventDTO);
    Event updateEvent(Integer eventId,EventDTO eventDTO);
    void deleteEvent(Integer eventId);
    Event getEvent(Integer eventId);
    Page<Event> getAllEvents(PageRequest pageRequest);
}
