package com.jpbazaar.service.email;

import com.jpbazaar.entity.Order;
import com.jpbazaar.entity.Payment;
import com.jpbazaar.entity.User;

public interface EmailService {

    void sendRegistrationEmail(User user);

    void sendOrderConfirmationEmail(Order order);

    void sendPaymentSuccessEmail(Order order, Payment payment);

    void sendPaymentFailureEmail(Order order, String reason);

    void sendOrderProcessingEmail(Order order);

    void sendOrderShippedEmail(Order order);

    void sendOutForDeliveryEmail(Order order);

    void sendOrderDeliveredEmail(Order order);

    void sendOrderCancelledEmail(Order order, String reason);

    void sendRefundCompletedEmail(Order order, Payment payment);
}
