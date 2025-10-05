package com.college.eventmanagement.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public class BookingDtos {

    public static class BookTicketRequest {
        @NotNull
        public Long eventId;
    }

    public static class TicketResponse {
        public Long id;
        public Long eventId;
        public Long userId;
        public String status;
        public String paymentStatus;
        public String qrCodeHash;
        public Instant purchasedAt;
        public Instant checkedInAt;
    }

    public static class QrValidationRequest {
        @NotNull
        public String qrCodeHash;
    }

    public static class QrValidationResponse {
        public boolean valid;
        public String message;
        public Long ticketId;
        public Long eventId;
        public Long userId;
    }
}
