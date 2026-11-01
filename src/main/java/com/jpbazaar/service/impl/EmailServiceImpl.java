package com.jpbazaar.service.impl;

import com.jpbazaar.entity.*;
import com.jpbazaar.repository.EmailNotificationLogRepository;
import com.jpbazaar.service.email.EmailService;
import com.jpbazaar.service.email.EmailTemplateService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final Optional<JavaMailSender> mailSender;
    private final EmailNotificationLogRepository emailLogRepository;
    private final EmailTemplateService emailTemplateService;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:concierge@jpbazaar.com}")
    private String mailFrom;

    @Autowired
    public EmailServiceImpl(
            @Autowired(required = false) JavaMailSender mailSender,
            EmailNotificationLogRepository emailLogRepository,
            EmailTemplateService emailTemplateService) {
        this.mailSender = Optional.ofNullable(mailSender);
        this.emailLogRepository = emailLogRepository;
        this.emailTemplateService = emailTemplateService;
    }

    @Override
    public void sendRegistrationEmail(User user) {
        if (user == null || user.getEmail() == null) {
            return;
        }
        if (emailLogRepository.existsByRecipientEmailAndNotificationTypeAndStatus(
                user.getEmail(), NotificationType.REGISTRATION_CONFIRMATION, NotificationStatus.SENT)) {
            log.info("Duplicate registration email avoided for {}", user.getEmail());
            return;
        }

        String subject = "Welcome to JPBazaar Elite Privileges";
        String htmlBody = emailTemplateService.buildRegistrationEmail(user);
        dispatchEmail(user.getEmail(), null, NotificationType.REGISTRATION_CONFIRMATION, subject, htmlBody);
    }

    @Override
    public void sendOrderConfirmationEmail(Order order) {
        if (order == null || order.getUser() == null) {
            return;
        }
        if (emailLogRepository.existsByOrderIdAndNotificationTypeAndStatus(
                order.getId(), NotificationType.ORDER_CONFIRMATION, NotificationStatus.SENT)) {
            log.info("Duplicate order confirmation email avoided for order #{}", order.getId());
            return;
        }

        String subject = "Order Confirmation #" + order.getId() + " - JPBazaar";
        String htmlBody = emailTemplateService.buildOrderConfirmationEmail(order);
        dispatchEmail(order.getUser().getEmail(), order.getId(), NotificationType.ORDER_CONFIRMATION, subject, htmlBody);
    }

    @Override
    public void sendPaymentSuccessEmail(Order order, Payment payment) {
        if (order == null || order.getUser() == null) {
            return;
        }
        if (emailLogRepository.existsByOrderIdAndNotificationTypeAndStatus(
                order.getId(), NotificationType.PAYMENT_SUCCESS, NotificationStatus.SENT)) {
            log.info("Duplicate payment success email avoided for order #{}", order.getId());
            return;
        }

        String subject = "Payment Verified for Order #" + order.getId() + " - JPBazaar";
        String htmlBody = emailTemplateService.buildPaymentSuccessEmail(order, payment);
        dispatchEmail(order.getUser().getEmail(), order.getId(), NotificationType.PAYMENT_SUCCESS, subject, htmlBody);
    }

    @Override
    public void sendPaymentFailureEmail(Order order, String reason) {
        if (order == null || order.getUser() == null) {
            return;
        }

        String subject = "Payment Transaction Alert - Order #" + order.getId();
        String htmlBody = emailTemplateService.buildPaymentFailureEmail(order, reason);
        dispatchEmail(order.getUser().getEmail(), order.getId(), NotificationType.PAYMENT_FAILURE, subject, htmlBody);
    }

    @Override
    public void sendOrderProcessingEmail(Order order) {
        if (order == null || order.getUser() == null) {
            return;
        }
        if (emailLogRepository.existsByOrderIdAndNotificationTypeAndStatus(
                order.getId(), NotificationType.ORDER_PROCESSING, NotificationStatus.SENT)) {
            return;
        }

        String subject = "Order #" + order.getId() + " Being Prepared - JPBazaar";
        String htmlBody = emailTemplateService.buildOrderProcessingEmail(order);
        dispatchEmail(order.getUser().getEmail(), order.getId(), NotificationType.ORDER_PROCESSING, subject, htmlBody);
    }

    @Override
    public void sendOrderShippedEmail(Order order) {
        if (order == null || order.getUser() == null) {
            return;
        }
        if (emailLogRepository.existsByOrderIdAndNotificationTypeAndStatus(
                order.getId(), NotificationType.ORDER_SHIPPED, NotificationStatus.SENT)) {
            return;
        }

        String subject = "Consignment Dispatched - Order #" + order.getId() + " - JPBazaar";
        String htmlBody = emailTemplateService.buildOrderShippedEmail(order);
        dispatchEmail(order.getUser().getEmail(), order.getId(), NotificationType.ORDER_SHIPPED, subject, htmlBody);
    }

    @Override
    public void sendOutForDeliveryEmail(Order order) {
        if (order == null || order.getUser() == null) {
            return;
        }
        if (emailLogRepository.existsByOrderIdAndNotificationTypeAndStatus(
                order.getId(), NotificationType.OUT_FOR_DELIVERY, NotificationStatus.SENT)) {
            return;
        }

        String subject = "Out for Delivery Today - Order #" + order.getId() + " - JPBazaar";
        String htmlBody = emailTemplateService.buildOutForDeliveryEmail(order);
        dispatchEmail(order.getUser().getEmail(), order.getId(), NotificationType.OUT_FOR_DELIVERY, subject, htmlBody);
    }

    @Override
    public void sendOrderDeliveredEmail(Order order) {
        if (order == null || order.getUser() == null) {
            return;
        }
        if (emailLogRepository.existsByOrderIdAndNotificationTypeAndStatus(
                order.getId(), NotificationType.ORDER_DELIVERED, NotificationStatus.SENT)) {
            return;
        }

        String subject = "Delivered - Order #" + order.getId() + " - JPBazaar";
        String htmlBody = emailTemplateService.buildOrderDeliveredEmail(order);
        dispatchEmail(order.getUser().getEmail(), order.getId(), NotificationType.ORDER_DELIVERED, subject, htmlBody);
    }

    @Override
    public void sendOrderCancelledEmail(Order order, String reason) {
        if (order == null || order.getUser() == null) {
            return;
        }
        if (emailLogRepository.existsByOrderIdAndNotificationTypeAndStatus(
                order.getId(), NotificationType.ORDER_CANCELLED, NotificationStatus.SENT)) {
            return;
        }

        String subject = "Order #" + order.getId() + " Cancellation Notice - JPBazaar";
        String htmlBody = emailTemplateService.buildOrderCancelledEmail(order, reason);
        dispatchEmail(order.getUser().getEmail(), order.getId(), NotificationType.ORDER_CANCELLED, subject, htmlBody);
    }

    @Override
    public void sendRefundCompletedEmail(Order order, Payment payment) {
        if (order == null || order.getUser() == null) {
            return;
        }
        if (emailLogRepository.existsByOrderIdAndNotificationTypeAndStatus(
                order.getId(), NotificationType.REFUND_COMPLETED, NotificationStatus.SENT)) {
            return;
        }

        String subject = "Refund Processed for Order #" + order.getId() + " - JPBazaar";
        String htmlBody = emailTemplateService.buildRefundCompletedEmail(order, payment);
        dispatchEmail(order.getUser().getEmail(), order.getId(), NotificationType.REFUND_COMPLETED, subject, htmlBody);
    }

    private void dispatchEmail(String recipient, Long orderId, NotificationType type, String subject, String htmlContent) {
        EmailNotificationLog logEntry = EmailNotificationLog.builder()
                .recipientEmail(recipient)
                .orderId(orderId)
                .notificationType(type)
                .subject(subject)
                .status(NotificationStatus.PENDING)
                .build();

        if (!mailEnabled) {
            log.info("Email notifications disabled via config. Skipping email: {} to {}", type, recipient);
            logEntry.setStatus(NotificationStatus.SKIPPED);
            logEntry.setErrorMessage("Email sending disabled in current environment (app.mail.enabled=false)");
            emailLogRepository.save(logEntry);
            return;
        }

        if (mailSender.isEmpty()) {
            log.warn("JavaMailSender bean not configured. Marking email as SKIPPED for {}", recipient);
            logEntry.setStatus(NotificationStatus.SKIPPED);
            logEntry.setErrorMessage("JavaMailSender not available");
            emailLogRepository.save(logEntry);
            return;
        }

        try {
            JavaMailSender sender = mailSender.get();
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom, "JPBazaar Concierge");
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            sender.send(message);

            logEntry.setStatus(NotificationStatus.SENT);
            logEntry.setSentAt(OffsetDateTime.now());
            log.info("Successfully sent {} email to {}", type, recipient);
        } catch (Exception ex) {
            log.error("Failed to transmit email {} to {}: {}", type, recipient, ex.getMessage(), ex);
            logEntry.setStatus(NotificationStatus.FAILED);
            logEntry.setErrorMessage(ex.getMessage() != null && ex.getMessage().length() > 950
                    ? ex.getMessage().substring(0, 950)
                    : ex.getMessage());
        } finally {
            try {
                emailLogRepository.save(logEntry);
            } catch (Exception e) {
                log.error("Failed to persist EmailNotificationLog: {}", e.getMessage());
            }
        }
    }
}
