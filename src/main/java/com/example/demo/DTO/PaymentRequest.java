package com.example.demo.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PaymentRequest {
    private String name;
    private String email;
    private long amount;
}
