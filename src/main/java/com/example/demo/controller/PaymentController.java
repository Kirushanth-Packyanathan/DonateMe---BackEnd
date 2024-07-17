package com.example.demo.controller;

import com.example.demo.DTO.PaymentRequest;
import com.stripe.exception.StripeException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/payment")
public class PaymentController {

    String STRIPE_API_KEY = System.getenv().get("STRIPE_API_KEY");

    @PostMapping()
    String hostedCheckout(@RequestBody PaymentRequest PaymentRequest) throws StripeException {
        return "Hello World!";
    }
}
