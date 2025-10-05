package com.college.eventmanagement.service.impl;

import com.college.eventmanagement.dto.AdminDtos;
import com.college.eventmanagement.entity.BookingStatus;
import com.college.eventmanagement.entity.Event;
import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.TicketRepository;
import com.college.eventmanagement.service.AdminService;
import com.college.eventmanagement.service.EmailService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final EmailService emailService;

    public AdminServiceImpl(EventRepository eventRepository, TicketRepository ticketRepository, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.emailService = emailService;
    }

    @Override
    public AdminDtos.EventMetricsResponse metrics(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        AdminDtos.EventMetricsResponse r = new AdminDtos.EventMetricsResponse();
        r.eventId = event.getId();
        r.capacity = event.getTotalTickets();
        r.ticketsSold = event.getTicketsSold();
        r.registrations = (int) ticketRepository.countByEventIdAndStatus(eventId, BookingStatus.CONFIRMED);
        r.checkedIn = (int) ticketRepository.countByEventIdAndStatus(eventId, BookingStatus.CHECKED_IN);
        r.revenue = event.isPaid() && event.getTicketPrice() != null ? event.getTicketPrice().multiply(new BigDecimal(r.ticketsSold)) : BigDecimal.ZERO;
        return r;
    }

    @Override
    public void sendBulkEmail(AdminDtos.BulkEmailRequest request) {
        var tickets = ticketRepository.findByEventId(request.eventId);
        tickets.forEach(t -> {
            String html = "<h3>Message from Organizer</h3><p>" + request.body + "</p>";
            emailService.sendTicketEmail(t.getUser().getEmail(), request.subject, html);
        });
    }

    @Override
    public List<AdminDtos.EventMetricsResponse> allMetrics() {
        return eventRepository.findAll().stream().map(e -> metrics(e.getId())).toList();
    }
}
