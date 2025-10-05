package com.college.eventmanagement.service;

import com.college.eventmanagement.dto.EventDtos;
import com.college.eventmanagement.entity.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    EventDtos.EventResponse createEvent(EventDtos.CreateEventRequest request);
    EventDtos.EventResponse updateEvent(Long id, EventDtos.UpdateEventRequest request);
    void deleteEvent(Long id);
    EventDtos.EventResponse publishEvent(Long id);
    EventDtos.EventResponse cancelEvent(Long id);
    EventDtos.EventResponse getEvent(Long id);
    List<EventDtos.EventResponse> listPublishedEvents();
    List<EventDtos.EventResponse> searchByDateRange(LocalDate start, LocalDate end);
}
