package com.college.eventmanagement.controller;

import com.college.eventmanagement.dto.FeedbackDtos;
import com.college.eventmanagement.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<FeedbackDtos.FeedbackResponse> create(@Valid @RequestBody FeedbackDtos.CreateFeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.addFeedback(request));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<FeedbackDtos.FeedbackResponse>> list(@PathVariable Long eventId) {
        return ResponseEntity.ok(feedbackService.listByEvent(eventId));
    }
}
