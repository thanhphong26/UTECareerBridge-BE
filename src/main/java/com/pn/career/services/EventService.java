package com.pn.career.services;

import com.pn.career.dtos.EventDTO;
import com.pn.career.dtos.EventTimelineDTO;
import com.pn.career.models.Event;
import com.pn.career.models.EventTimeline;
import com.pn.career.models.EventType;
import com.pn.career.models.NotificationType;
import com.pn.career.repositories.EventRepository;
import com.pn.career.repositories.EventTimelineRepository;
import com.pn.career.responses.EventResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService{
    private final EventRepository eventRepository;
    private final EventTimelineRepository eventTimelineRepository;
    private final CloudinaryService cloudinaryService;
    private final NotificationService notificationService;
    @Override
    public Page<Event> getAllEvents(EventType eventType, PageRequest pageRequest) {
        return eventRepository.findAllByEventTypeOrderByEventDate(eventType, pageRequest);
    }
    @Override
    public EventResponse createEvent(EventDTO eventDTO) {
        Event event = Event.builder()
                .eventTitle(eventDTO.getEventTitle())
                .eventDescription(eventDTO.getEventDescription())
                .eventDate(eventDTO.getEventDate())
                .eventLocation(eventDTO.getEventLocation())
                .eventImage(eventDTO.getEventImage())
                .maxParticipants(eventDTO.getMaxParticipants())
                .currentParticipants(eventDTO.getCurrentParticipants())
                .eventType(eventDTO.getEventType())
                .build();

        // Lưu event trước
        Event savedEvent = eventRepository.save(event);

        // Tạo danh sách timeline với event đã được lưu
        List<EventTimeline> timelines = new ArrayList<>();
        for (EventTimelineDTO timelineDTO : eventDTO.getTimeline()) {
            EventTimeline timeline = EventTimeline.builder()
                    .event(savedEvent)
                    .timelineTitle(timelineDTO.getTimelineTitle())
                    .timelineDescription(timelineDTO.getTimelineDescription())
                    .timelineStart(timelineDTO.getTimelineStart())
                    .build();
            timelines.add(timeline);
        }
        // Lưu các timeline
        eventTimelineRepository.saveAll(timelines);

        // Cập nhật event với danh sách timeline
        savedEvent.setEventTimelines(timelines);
        savedEvent = eventRepository.save(savedEvent);
        String title = "Sự kiện mới sắp diễn ra! Đừng bỏ lỡ! \n " + savedEvent.getEventTitle();
        notificationService.sendEventNotification(savedEvent.getEventTitle(), savedEvent.getEventDescription(), NotificationType.BROADCAST, savedEvent.getEventDate(), savedEvent.getEventLocation(), "/event-detail/" + savedEvent.getEventId());
        return EventResponse.from(savedEvent);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Integer eventId, EventDTO eventDTO) {
        Event existingEvent = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));
        List<EventTimeline> existingTimelines = existingEvent.getEventTimelines();
        existingEvent.setEventTitle(eventDTO.getEventTitle());
        existingEvent.setEventDescription(eventDTO.getEventDescription());
        existingEvent.setEventDate(eventDTO.getEventDate());
        existingEvent.setEventLocation(eventDTO.getEventLocation());
        existingEvent.setEventImage(eventDTO.getEventImage());
        existingEvent.setMaxParticipants(eventDTO.getMaxParticipants());
        existingEvent.setCurrentParticipants(eventDTO.getCurrentParticipants());
        existingEvent.setEventType(eventDTO.getEventType());

        List<Integer> newTimelineIds = eventDTO.getTimeline().stream()
                .map(EventTimelineDTO::getTimelineId)
                .filter(Objects::nonNull) // Chỉ lấy những timeline đã có ID
                .collect(Collectors.toList());

        // Xóa các timeline không tồn tại trong danh sách mới
        existingTimelines.removeIf(timeline -> !newTimelineIds.contains(timeline.getTimelineId()));

        for (EventTimelineDTO timelineDTO : eventDTO.getTimeline()) {
            if (timelineDTO.getTimelineId() != null) {
                // Cập nhật timeline đã tồn tại
                existingTimelines.stream()
                        .filter(timeline -> timeline.getTimelineId().equals(timelineDTO.getTimelineId()))
                        .findFirst()
                        .ifPresent(existingTimeline -> {
                            existingTimeline.setEvent(existingEvent);
                            existingTimeline.setTimelineTitle(timelineDTO.getTimelineTitle());
                            existingTimeline.setTimelineDescription(timelineDTO.getTimelineDescription());
                            existingTimeline.setTimelineStart(timelineDTO.getTimelineStart());
                        });
            } else {
                existingTimelines.add(EventTimeline.builder()
                        .event(existingEvent)
                        .timelineTitle(timelineDTO.getTimelineTitle())
                        .timelineDescription(timelineDTO.getTimelineDescription())
                        .timelineStart(timelineDTO.getTimelineStart())
                        .build());
            }
        }
        existingEvent.setEventTimelines(existingTimelines);
        Event savedEvent = eventRepository.save(existingEvent);
        return EventResponse.from(savedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Integer eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public EventResponse getEventById(Integer eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));
        return EventResponse.from(event);
    }

    @Override
    public Integer countEventUpcomming(LocalDateTime dateNow) {
        List<Event> events = eventRepository.findAllByEventDateAfter(dateNow);
        return events.size();
    }

    @Override
    public List<EventResponse> getAllEventUpcomming(LocalDateTime dateNow, Integer limit) {
        List<Event> events = eventRepository.findAllByEventDateAfter(dateNow);
        if (events.size() > limit) {
            events = events.subList(0, limit);
        }
        return events.stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());

    }

    @Override
    public Integer countEventsByYear(Integer year) {
        return eventRepository.countEventsByYear(year);
    }
}
