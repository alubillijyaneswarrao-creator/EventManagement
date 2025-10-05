package com.college.eventmanagement.service.impl;

import com.college.eventmanagement.dto.FeedbackDtos;
import com.college.eventmanagement.entity.Event;
import com.college.eventmanagement.entity.Feedback;
import com.college.eventmanagement.exception.ApiException;
import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.FeedbackRepository;
import com.college.eventmanagement.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;
    private final UserServiceImpl userService;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, EventRepository eventRepository, UserServiceImpl userService) {
        this.feedbackRepository = feedbackRepository;
        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    @Override
    public FeedbackDtos.FeedbackResponse addFeedback(FeedbackDtos.CreateFeedbackRequest request) {
        var event = eventRepository.findById(request.eventId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found"));
        var user = userService.getCurrentUser();
        Feedback fb = new Feedback();
        fb.setEvent(event);
        fb.setUser(user);
        fb.setRating(request.rating);
        fb.setComments(request.comments);
        fb.setCreatedAt(Instant.now());
        feedbackRepository.save(fb);
        return toResponse(fb);
    }

    @Override
    public List<FeedbackDtos.FeedbackResponse> listByEvent(Long eventId) {
        return feedbackRepository.findByEventId(eventId).stream().map(this::toResponse).toList();
    }

    private FeedbackDtos.FeedbackResponse toResponse(Feedback f) {
        var r = new FeedbackDtos.FeedbackResponse();
        r.id = f.getId();
        r.eventId = f.getEvent().getId();
        r.userId = f.getUser().getId();
        r.rating = f.getRating();
        r.comments = f.getComments();
        r.createdAt = f.getCreatedAt();
        return r;
    }
}
