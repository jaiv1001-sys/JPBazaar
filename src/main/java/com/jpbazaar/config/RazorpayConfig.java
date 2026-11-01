package com.jpbazaar.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RazorpayConfig {

    @Value("${razorpay.key-id:rzp_test_JPBazaarDemoKey123}")
    private String keyId;

    @Value("${razorpay.key-secret:JPBazaarDemoSecretKey456}")
    private String keySecret;

    @Value("${razorpay.webhook-secret:JPBazaarWebhookSecret789}")
    private String webhookSecret;

    @Value("${razorpay.currency:INR}")
    private String currency;

    @Value("${razorpay.enabled:true}")
    private boolean enabled;

    /**
     * Determines whether the current key configuration is real live/test credentials
     * or a fallback test placeholder.
     */
    public boolean isPlaceholder() {
        return keyId == null || keyId.isBlank() || keyId.contains("placeholder") || keyId.contains("DemoKey");
    }
}
