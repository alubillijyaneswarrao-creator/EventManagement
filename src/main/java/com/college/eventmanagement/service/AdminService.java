package com.college.eventmanagement.service;

import com.college.eventmanagement.dto.AdminDtos;
import java.util.List;

public interface AdminService {
    AdminDtos.EventMetricsResponse metrics(Long eventId);
    void sendBulkEmail(AdminDtos.BulkEmailRequest request);
    List<AdminDtos.EventMetricsResponse> allMetrics();
}
