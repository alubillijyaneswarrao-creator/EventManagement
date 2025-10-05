package com.college.eventmanagement.service.impl;

import com.college.eventmanagement.dto.BookingDtos;
import com.college.eventmanagement.entity.*;
import com.college.eventmanagement.exception.ApiException;
import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.TicketRepository;
import com.college.eventmanagement.repository.UserRepository;
import com.college.eventmanagement.security.JwtService;
import com.college.eventmanagement.service.BookingService;
import com.college.eventmanagement.service.EmailService;
import com.college.eventmanagement.util.QrCodeUtil;
import javax.imageio.ImageIO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserServiceImpl userService;
    private final EmailService emailService;
    private final JwtService jwtService;

    public BookingServiceImpl(TicketRepository ticketRepository,
                              EventRepository eventRepository,
                              UserServiceImpl userService,
                              EmailService emailService,
                              JwtService jwtService) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    @Override
    public BookingDtos.TicketResponse bookTicket(BookingDtos.BookTicketRequest request) {
        User user = userService.getCurrentUser();
        Event event = eventRepository.findById(request.eventId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found"));
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Event not available for booking");
        }
        if (event.getTicketsSold() >= event.getTotalTickets()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Sold out");
        }
        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setUser(user);
        ticket.setStatus(BookingStatus.CONFIRMED); // assume payment success for demo
        ticket.setPaymentStatus(PaymentStatus.SUCCESS);
        ticket.setPurchasedAt(Instant.now());

        Map<String, Object> claims = new HashMap<>();
        claims.put("tid", UUID.randomUUID().toString());
        claims.put("uid", user.getId());
        claims.put("eid", event.getId());
        String qrToken = jwtService.generateQrToken(user.getEmail(), claims);
        ticket.setQrCodeHash(qrToken);
        ticketRepository.save(ticket);

        event.setTicketsSold(event.getTicketsSold() + 1);
        eventRepository.save(event);

        sendTicketEmail(user, event, qrToken);

        return toResponse(ticket);
    }

    @Override
    public BookingDtos.QrValidationResponse validateQr(String qrHash) {
        var resp = new BookingDtos.QrValidationResponse();
        try {
            var jws = jwtService.parseToken(qrHash);
            String email = jws.getBody().getSubject();
            Long eventId = ((Number) jws.getBody().get("eid")).longValue();
            Long uid = ((Number) jws.getBody().get("uid")).longValue();
            Optional<Ticket> ticketOpt = ticketRepository.findByQrCodeHash(qrHash);
            if (ticketOpt.isEmpty()) {
                resp.valid = false;
                resp.message = "Ticket not found";
                return resp;
            }
            Ticket t = ticketOpt.get();
            resp.valid = true;
            resp.message = "Valid";
            resp.ticketId = t.getId();
            resp.eventId = eventId;
            resp.userId = uid;
            return resp;
        } catch (Exception e) {
            resp.valid = false;
            resp.message = "Invalid or expired QR";
            return resp;
        }
    }

    @Override
    public BookingDtos.TicketResponse checkIn(String qrHash) {
        BookingDtos.QrValidationResponse validate = validateQr(qrHash);
        if (!validate.valid) {
            throw new ApiException(HttpStatus.BAD_REQUEST, validate.message);
        }
        Ticket ticket = ticketRepository.findByQrCodeHash(qrHash).orElseThrow();
        ticket.setStatus(BookingStatus.CHECKED_IN);
        ticket.setCheckedInAt(Instant.now());
        ticketRepository.save(ticket);
        return toResponse(ticket);
    }

    @Override
    public List<BookingDtos.TicketResponse> listMyTickets() {
        User user = userService.getCurrentUser();
        return ticketRepository.findByUserId(user.getId()).stream().map(this::toResponse).toList();
    }

    @Override
    public List<BookingDtos.TicketResponse> listEventTickets(Long eventId) {
        return ticketRepository.findByEventId(eventId).stream().map(this::toResponse).toList();
    }

    private void sendTicketEmail(User user, Event event, String qrToken) {
        try {
            var img = QrCodeUtil.generateQr(qrToken, 256, 256);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, "png", os);
            String base64 = Base64.getEncoder().encodeToString(os.toByteArray());
            String html = "<h3>Your Ticket for " + event.getTitle() + "</h3>" +
                    "<p>Date: " + event.getEventDate() + "</p>" +
                    "<p>Location: " + event.getLocation() + "</p>" +
                    "<img src=\"data:image/png;base64," + base64 + "\" alt=\"QR Code\"/>";
            emailService.sendTicketEmail(user.getEmail(), "Ticket - " + event.getTitle(), html);
        } catch (Exception e) {
            // ignore mailing failure for core flow
        }
    }

    private BookingDtos.TicketResponse toResponse(Ticket t) {
        BookingDtos.TicketResponse r = new BookingDtos.TicketResponse();
        r.id = t.getId();
        r.eventId = t.getEvent().getId();
        r.userId = t.getUser().getId();
        r.status = t.getStatus().name();
        r.paymentStatus = t.getPaymentStatus().name();
        r.qrCodeHash = t.getQrCodeHash();
        r.purchasedAt = t.getPurchasedAt();
        r.checkedInAt = t.getCheckedInAt();
        return r;
    }
}
