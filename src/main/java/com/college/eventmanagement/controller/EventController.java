package com.college.eventmanagement.controller;

import com.college.eventmanagement.dto.EventDtos;
import com.college.eventmanagement.service.EventService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventDtos.EventResponse>> listPublished() {
        return ResponseEntity.ok(eventService.listPublishedEvents());
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventDtos.EventResponse>> search(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(eventService.searchByDateRange(start, end));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDtos.EventResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @PostMapping
    public ResponseEntity<EventDtos.EventResponse> create(@Valid @RequestBody EventDtos.CreateEventRequest request) {
        return ResponseEntity.ok(eventService.createEvent(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDtos.EventResponse> update(@PathVariable Long id, @Valid @RequestBody EventDtos.UpdateEventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<EventDtos.EventResponse> publish(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.publishEvent(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<EventDtos.EventResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.cancelEvent(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
