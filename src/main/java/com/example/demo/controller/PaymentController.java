package com.example.demo.controller;

import com.example.demo.DTO.PaymentRequest;
import com.example.demo.Utils.CustomerUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/payment")
public class PaymentController {

    private final String STRIPE_API_KEY = System.getenv().get("STRIPE_API_KEY");

    @PostMapping()
    public String hostedCheckout(@RequestBody PaymentRequest paymentRequest) throws StripeException {
        Stripe.apiKey = STRIPE_API_KEY;
        String clientBaseURL = System.getenv().get("CLIENT_BASE_URL");

        // Find or create customer
        Customer customer = CustomerUtil.findOrCreateCustomer(paymentRequest.getEmail(), paymentRequest.getName());

        // Create checkout session
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomer(customer.getId())
                .setSuccessUrl(clientBaseURL + "/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(clientBaseURL + "/failure");

        // Add the single payment amount as a line item
        paramsBuilder.addLineItem(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd") // Set your currency here
                                        .setUnitAmount(paymentRequest.getAmount())
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Total Payment")
                                                        .build()
                                        )
                                        .build()
                        )
                        .build()
        );

        Session session = Session.create(paramsBuilder.build());

        return session.getUrl();
    }
}
