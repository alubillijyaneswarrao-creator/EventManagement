package com.college.eventmanagement.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;

public class EventDtos {
    public static class CreateEventRequest {
        @NotBlank
        public String title;
        public String description;
        public String category;
        public String location;
        @NotNull
        public LocalDate eventDate;
        @NotNull
        public LocalTime startTime;
        @NotNull
        public LocalTime endTime;
        public boolean paid;
        @PositiveOrZero
        public BigDecimal ticketPrice;
        @Positive
        public int totalTickets;
        public String imageUrl;
    }

    public static class UpdateEventRequest extends CreateEventRequest {
    }

    public static class EventResponse {
        public Long id;
        public String title;
        public String description;
        public String category;
        public String location;
        public LocalDate eventDate;
        public LocalTime startTime;
        public LocalTime endTime;
        public boolean paid;
        public BigDecimal ticketPrice;
        public int totalTickets;
        public int ticketsSold;
        public String imageUrl;
        public String status;
        public Long organizerId;
        public double averageRating;
    }

    public static class EventListResponse {
        public List<EventResponse> events;
    }
}
