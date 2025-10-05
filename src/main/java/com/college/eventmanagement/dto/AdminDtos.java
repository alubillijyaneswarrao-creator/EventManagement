package com.college.eventmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AdminDtos {

    public static class EventMetricsResponse {
        public Long eventId;
        public int capacity;
        public int ticketsSold;
        public int registrations;
        public int checkedIn;
        public BigDecimal revenue;
    }

    public static class BulkEmailRequest {
        @NotNull
        public Long eventId;
        @NotBlank
        public String subject;
        @NotBlank
        public String body;
    }
}
