package com.college.eventmanagement.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;

public class FeedbackDtos {
    public static class CreateFeedbackRequest {
        @NotNull
        public Long eventId;
        @Min(1) @Max(5)
        public int rating;
        public String comments;
    }

    public static class FeedbackResponse {
        public Long id;
        public Long eventId;
        public Long userId;
        public int rating;
        public String comments;
        public Instant createdAt;
    }
}
