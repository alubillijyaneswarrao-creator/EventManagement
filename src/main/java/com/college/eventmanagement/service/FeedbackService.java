package com.college.eventmanagement.service;

import com.college.eventmanagement.dto.FeedbackDtos;
import java.util.List;

public interface FeedbackService {
    FeedbackDtos.FeedbackResponse addFeedback(FeedbackDtos.CreateFeedbackRequest request);
    List<FeedbackDtos.FeedbackResponse> listByEvent(Long eventId);
}
