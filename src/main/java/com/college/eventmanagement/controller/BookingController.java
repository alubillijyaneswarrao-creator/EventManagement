package com.college.eventmanagement.controller;

import com.college.eventmanagement.dto.BookingDtos;
import com.college.eventmanagement.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDtos.TicketResponse> book(@Valid @RequestBody BookingDtos.BookTicketRequest request) {
        return ResponseEntity.ok(bookingService.bookTicket(request));
    }

    @PostMapping("/validate")
    public ResponseEntity<BookingDtos.QrValidationResponse> validate(@Valid @RequestBody BookingDtos.QrValidationRequest request) {
        return ResponseEntity.ok(bookingService.validateQr(request.qrCodeHash));
    }

    @PostMapping("/check-in")
    public ResponseEntity<BookingDtos.TicketResponse> checkIn(@Valid @RequestBody BookingDtos.QrValidationRequest request) {
        return ResponseEntity.ok(bookingService.checkIn(request.qrCodeHash));
    }

    @GetMapping("/me")
    public ResponseEntity<List<BookingDtos.TicketResponse>> myTickets() {
        return ResponseEntity.ok(bookingService.listMyTickets());
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingDtos.TicketResponse>> eventTickets(@PathVariable Long eventId) {
        return ResponseEntity.ok(bookingService.listEventTickets(eventId));
    }
}
