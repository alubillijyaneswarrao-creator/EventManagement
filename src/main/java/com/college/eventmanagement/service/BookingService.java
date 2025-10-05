package com.college.eventmanagement.service;

import com.college.eventmanagement.dto.BookingDtos;
import java.util.List;

public interface BookingService {
    BookingDtos.TicketResponse bookTicket(BookingDtos.BookTicketRequest request);
    BookingDtos.QrValidationResponse validateQr(String qrHash);
    BookingDtos.TicketResponse checkIn(String qrHash);
    List<BookingDtos.TicketResponse> listMyTickets();
    List<BookingDtos.TicketResponse> listEventTickets(Long eventId);
}
