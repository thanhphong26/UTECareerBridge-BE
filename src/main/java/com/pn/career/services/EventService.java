package com.pn.career.services;

import com.pn.career.dtos.EventDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.InvalidMultipartFile;
import com.pn.career.models.Event;
import com.pn.career.repositories.EventRepository;
import com.pn.career.utils.SlugConverter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService{
    private final EventRepository eventRepository;
    private final CloudinaryService cloudinaryService;
    @Override
    @Transactional
    public Event createEvent(EventDTO eventDTO) {
        try{
            Event event=Event.builder()
                    .eventTitle(eventDTO.getEventTitle())
                    .eventDescription(eventDTO.getEventDescription())
                    .eventDate(eventDTO.getEventDate())
                    .eventLocation(eventDTO.getEventLocation())
                    .build();
            if(!eventDTO.getEventImage().isEmpty()){
                if(!cloudinaryService.isValidImage(eventDTO.getEventImage())){
                    throw new InvalidMultipartFile("Ảnh tải lên không đúng định dạng. Vui lòng upload ảnh có định dạng jpeg hoặc png");
                }
                String eventNameSlug= SlugConverter.toSlug(eventDTO.getEventTitle());
                String imageUrl=cloudinaryService.uploadImageEventToCloudinary(eventDTO.getEventImage(), eventNameSlug);
                event.setEventImage(imageUrl);
            }

            return eventRepository.save(event);
        }catch (IOException e){
            throw  new RuntimeException("Đã xảy ra lỗi khi upload hình ảnh. Vui lòng thử lại sau");
        }
    }

    @Override
    @Transactional
    public Event updateEvent(Integer eventId, EventDTO eventDTO) {
        Event event=getEvent(eventId);
        try{
            String imgUrl=!event.getEventImage().isEmpty()?event.getEventImage():"";
            if(!eventDTO.getEventImage().isEmpty()){
                String eventNameSlug= SlugConverter.toSlug(eventDTO.getEventTitle());
                imgUrl=cloudinaryService.uploadImageEventToCloudinary(eventDTO.getEventImage(), eventNameSlug);
            }
            event.setEventTitle(eventDTO.getEventTitle());
            event.setEventDescription(eventDTO.getEventDescription());
            event.setEventDate(eventDTO.getEventDate());
            event.setEventLocation(eventDTO.getEventLocation());
            event.setEventImage(imgUrl);
            return eventRepository.save(event);
        }catch (IOException e) {
            throw new RuntimeException("Đã xảy ra lỗi khi upload hình ảnh. Vui lòng thử lại sau");
        }
    }

    @Override
    public void deleteEvent(Integer eventId) {
        Event event=getEvent(eventId);
        eventRepository.delete(event);
    }

    @Override
    public Event getEvent(Integer eventId) {
        return eventRepository.findById(eventId).orElseThrow(()->new DataNotFoundException("Không tìm thấy sự kiện tương ứng"));
    }

    @Override
    public Page<Event> getAllEvents(PageRequest pageRequest) {
        return eventRepository.findAll(pageRequest);
    }
}
