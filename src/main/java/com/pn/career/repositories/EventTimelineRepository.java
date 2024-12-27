package com.pn.career.repositories;

import com.pn.career.models.EventTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EventTimelineRepository extends JpaRepository<EventTimeline, Integer> {
}
