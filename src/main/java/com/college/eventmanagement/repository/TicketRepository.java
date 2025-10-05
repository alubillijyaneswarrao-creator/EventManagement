package com.college.eventmanagement.repository;

import com.college.eventmanagement.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByQrCodeHash(String qrCodeHash);
    long countByEventIdAndStatus(Long eventId, BookingStatus status);
    List<Ticket> findByEventId(Long eventId);
    List<Ticket> findByUserId(Long userId);
}
