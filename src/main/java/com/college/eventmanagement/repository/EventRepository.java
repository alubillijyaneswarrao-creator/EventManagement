package com.college.eventmanagement.repository;

import com.college.eventmanagement.entity.Event;
import com.college.eventmanagement.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
    List<Event> findByEventDateBetween(LocalDate start, LocalDate end);
}
