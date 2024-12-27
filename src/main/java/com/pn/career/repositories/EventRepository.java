package com.pn.career.repositories;

import com.pn.career.models.Event;
import com.pn.career.models.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Integer> {
    //get all events by event type and order by created at desc if event type is null return all events
    @Query("SELECT e FROM Event e WHERE (:eventType IS NULL OR e.eventType = :eventType) AND e.eventDate > CURRENT_DATE ORDER BY e.createdAt DESC")
    Page<Event> findAllByEventTypeOrderByCreatedAt(@Param("eventType") EventType eventType, PageRequest pageRequest);
}
