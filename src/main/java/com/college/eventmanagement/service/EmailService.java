package com.college.eventmanagement.service;

public interface EmailService {
    void sendTicketEmail(String to, String subject, String htmlBody);
}
