package com.example.demo.DTO;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {
}
