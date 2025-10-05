package com.college.eventmanagement.service.impl;

import com.college.eventmanagement.dto.EventDtos;
import com.college.eventmanagement.entity.*;
import com.college.eventmanagement.exception.ApiException;
import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.FeedbackRepository;
import com.college.eventmanagement.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserServiceImpl userService;

    public EventServiceImpl(EventRepository eventRepository, FeedbackRepository feedbackRepository, UserServiceImpl userService) {
        this.eventRepository = eventRepository;
        this.feedbackRepository = feedbackRepository;
        this.userService = userService;
    }

    @Override
    public EventDtos.EventResponse createEvent(EventDtos.CreateEventRequest request) {
        User organizer = userService.getCurrentUser();
        if (organizer.getRole() == Role.ATTENDEE) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only organizers or admin can create events");
        }
        Event event = new Event();
        apply(request, event);
        event.setOrganizer(organizer);
        event.setStatus(EventStatus.DRAFT);
        eventRepository.save(event);
        return toResponse(event);
    }

    @Override
    public EventDtos.EventResponse updateEvent(Long id, EventDtos.UpdateEventRequest request) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found"));
        ensureOwnerOrAdmin(event);
        apply(request, event);
        eventRepository.save(event);
        return toResponse(event);
    }

    @Override
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found"));
        ensureOwnerOrAdmin(event);
        eventRepository.delete(event);
    }

    @Override
    public EventDtos.EventResponse publishEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found"));
        ensureOwnerOrAdmin(event);
        event.setStatus(EventStatus.PUBLISHED);
        eventRepository.save(event);
        return toResponse(event);
    }

    @Override
    public EventDtos.EventResponse cancelEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found"));
        ensureOwnerOrAdmin(event);
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
        return toResponse(event);
    }

    @Override
    public EventDtos.EventResponse getEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found"));
        return toResponse(event);
    }

    @Override
    public List<EventDtos.EventResponse> listPublishedEvents() {
        return eventRepository.findByStatus(EventStatus.PUBLISHED)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<EventDtos.EventResponse> searchByDateRange(LocalDate start, LocalDate end) {
        return eventRepository.findByEventDateBetween(start, end)
                .stream().filter(e -> e.getStatus() == EventStatus.PUBLISHED)
                .map(this::toResponse).collect(Collectors.toList());
    }

    private void apply(EventDtos.CreateEventRequest req, Event e) {
        e.setTitle(req.title);
        e.setDescription(req.description);
        e.setCategory(req.category);
        e.setLocation(req.location);
        e.setEventDate(req.eventDate);
        e.setStartTime(req.startTime);
        e.setEndTime(req.endTime);
        e.setPaid(req.paid);
        e.setTicketPrice(req.ticketPrice);
        e.setTotalTickets(req.totalTickets);
        e.setImageUrl(req.imageUrl);
    }

    private void ensureOwnerOrAdmin(Event event) {
        User current = userService.getCurrentUser();
        if (!(current.getRole() == Role.ADMIN || event.getOrganizer().getId().equals(current.getId()))) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not allowed");
        }
    }

    private EventDtos.EventResponse toResponse(Event e) {
        EventDtos.EventResponse r = new EventDtos.EventResponse();
        r.id = e.getId();
        r.title = e.getTitle();
        r.description = e.getDescription();
        r.category = e.getCategory();
        r.location = e.getLocation();
        r.eventDate = e.getEventDate();
        r.startTime = e.getStartTime();
        r.endTime = e.getEndTime();
        r.paid = e.isPaid();
        r.ticketPrice = e.getTicketPrice();
        r.totalTickets = e.getTotalTickets();
        r.ticketsSold = e.getTicketsSold();
        r.imageUrl = e.getImageUrl();
        r.status = e.getStatus().name();
        r.organizerId = e.getOrganizer() != null ? e.getOrganizer().getId() : null;
        var ratings = feedbackRepository.findByEventId(e.getId())
                .stream().mapToInt(f -> f.getRating()).summaryStatistics();
        r.averageRating = ratings.getCount() == 0 ? 0.0 : ratings.getAverage();
        return r;
    }
}
