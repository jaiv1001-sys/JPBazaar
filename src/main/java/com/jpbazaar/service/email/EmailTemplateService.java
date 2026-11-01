package com.jpbazaar.service.email;

import com.jpbazaar.entity.Order;
import com.jpbazaar.entity.OrderItem;
import com.jpbazaar.entity.Payment;
import com.jpbazaar.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailTemplateService {

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public String buildRegistrationEmail(User user) {
        String name = user.getFirstName() != null ? user.getFirstName() : "Valued Patron";
        String content = """
            <h2 style="color: #D4AF37; font-size: 22px; margin-top: 0; margin-bottom: 16px; font-weight: 600;">Welcome to JPBazaar Elite</h2>
            <p style="color: #E5E5E5; font-size: 15px; line-height: 1.6; margin-bottom: 20px;">
                Dear %s,
            </p>
            <p style="color: #CCCCCC; font-size: 14px; line-height: 1.6; margin-bottom: 24px;">
                We are honored to welcome you to <strong>JPBazaar</strong>, where curations of elegance meet exemplary artisanal craftsmanship.
                Your personal client account has been successfully created and secured with industry-leading encryption.
            </p>
            <div style="background-color: #1A1A1A; border: 1px solid rgba(212, 175, 55, 0.25); border-radius: 8px; padding: 20px; margin-bottom: 24px;">
                <p style="margin: 0 0 8px 0; color: #888888; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">Account Registered</p>
                <p style="margin: 0; color: #F5E084; font-size: 16px; font-weight: 600;">%s</p>
            </div>
            <p style="text-align: center; margin: 30px 0;">
                <a href="%s" style="background: linear-gradient(135deg, #D4AF37 0%%, #AA771C 100%%); color: #0A0A0A; text-decoration: none; font-weight: 700; font-size: 14px; padding: 14px 32px; border-radius: 6px; display: inline-block; letter-spacing: 1px; text-transform: uppercase;">
                    Explore The Boutique
                </a>
            </p>
        """.formatted(name, user.getEmail(), frontendUrl);

        return wrapInLuxuryLayout("Welcome to JPBazaar", content);
    }

    public String buildOrderConfirmationEmail(Order order) {
        String customerName = order.getUser() != null ? order.getUser().getFirstName() + " " + order.getUser().getLastName() : "Valued Patron";
        String trackingUrl = frontendUrl + "/account/orders/" + order.getId() + "/track";

        String itemsHtml = renderItemsTable(order.getItems());
        String addressHtml = renderAddressBlock(order);

        String content = """
            <div style="border-left: 3px solid #D4AF37; padding-left: 16px; margin-bottom: 24px;">
                <span style="color: #D4AF37; font-size: 12px; text-transform: uppercase; letter-spacing: 2px;">Order Confirmed</span>
                <h2 style="color: #FFFFFF; font-size: 22px; margin: 4px 0 0 0; font-weight: 600;">Acquisition #%d</h2>
            </div>
            <p style="color: #CCCCCC; font-size: 14px; line-height: 1.6; margin-bottom: 20px;">
                Dear %s, thank you for your patronage. Your order has been registered and is queued for concierge handling and preparation.
            </p>
            %s
            <div style="background-color: #1A1A1A; border: 1px solid rgba(212, 175, 55, 0.2); border-radius: 8px; padding: 18px; margin: 24px 0;">
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr>
                        <td style="color: #AAAAAA; font-size: 14px; padding: 4px 0;">Total Amount:</td>
                        <td style="color: #D4AF37; font-size: 18px; font-weight: 700; text-align: right; padding: 4px 0;">₹%s</td>
                    </tr>
                    <tr>
                        <td style="color: #AAAAAA; font-size: 13px; padding: 4px 0;">Order Status:</td>
                        <td style="color: #FFFFFF; font-size: 13px; font-weight: 600; text-align: right; padding: 4px 0;">%s</td>
                    </tr>
                </table>
            </div>
            %s
            <p style="text-align: center; margin: 32px 0 16px 0;">
                <a href="%s" style="background: linear-gradient(135deg, #D4AF37 0%%, #AA771C 100%%); color: #0A0A0A; text-decoration: none; font-weight: 700; font-size: 13px; padding: 12px 28px; border-radius: 6px; display: inline-block; letter-spacing: 1px; text-transform: uppercase;">
                    Track Consignment
                </a>
            </p>
        """.formatted(order.getId(), customerName, itemsHtml, order.getTotalAmount(), order.getStatus(), addressHtml, trackingUrl);

        return wrapInLuxuryLayout("Order Confirmation #" + order.getId(), content);
    }

    public String buildPaymentSuccessEmail(Order order, Payment payment) {
        String trackingUrl = frontendUrl + "/account/orders/" + order.getId() + "/track";
        String txnId = payment != null && payment.getTransactionId() != null ? payment.getTransactionId() : "N/A";
        String method = payment != null && payment.getMethod() != null ? payment.getMethod().name() : "ONLINE";
        BigDecimal amount = payment != null && payment.getAmount() != null ? payment.getAmount() : order.getTotalAmount();

        String content = """
            <div style="border-left: 3px solid #10B981; padding-left: 16px; margin-bottom: 24px;">
                <span style="color: #10B981; font-size: 12px; text-transform: uppercase; letter-spacing: 2px;">Payment Settled</span>
                <h2 style="color: #FFFFFF; font-size: 22px; margin: 4px 0 0 0; font-weight: 600;">Payment Verified for Order #%d</h2>
            </div>
            <p style="color: #CCCCCC; font-size: 14px; line-height: 1.6; margin-bottom: 20px;">
                Your payment has been successfully authorized and captured. A formal tax invoice has been generated for your records.
            </p>
            <div style="background-color: #1A1A1A; border: 1px solid rgba(212, 175, 55, 0.2); border-radius: 8px; padding: 20px; margin-bottom: 24px;">
                <table style="width: 100%%; border-collapse: collapse; font-size: 14px;">
                    <tr>
                        <td style="color: #888888; padding: 6px 0;">Transaction Reference:</td>
                        <td style="color: #FFFFFF; font-weight: 600; text-align: right; padding: 6px 0;">%s</td>
                    </tr>
                    <tr>
                        <td style="color: #888888; padding: 6px 0;">Payment Instrument:</td>
                        <td style="color: #FFFFFF; font-weight: 600; text-align: right; padding: 6px 0;">%s</td>
                    </tr>
                    <tr>
                        <td style="color: #888888; padding: 6px 0;">Amount Paid:</td>
                        <td style="color: #D4AF37; font-weight: 700; font-size: 16px; text-align: right; padding: 6px 0;">₹%s</td>
                    </tr>
                    <tr>
                        <td style="color: #888888; padding: 6px 0;">Payment Status:</td>
                        <td style="color: #10B981; font-weight: 600; text-align: right; padding: 6px 0;">SUCCESS</td>
                    </tr>
                </table>
            </div>
            <p style="text-align: center; margin: 30px 0;">
                <a href="%s" style="background: linear-gradient(135deg, #D4AF37 0%%, #AA771C 100%%); color: #0A0A0A; text-decoration: none; font-weight: 700; font-size: 13px; padding: 12px 28px; border-radius: 6px; display: inline-block; letter-spacing: 1px; text-transform: uppercase;">
                    View Order Details
                </a>
            </p>
        """.formatted(order.getId(), txnId, method, amount, trackingUrl);

        return wrapInLuxuryLayout("Payment Received - Order #" + order.getId(), content);
    }

    public String buildPaymentFailureEmail(Order order, String reason) {
        String checkoutUrl = frontendUrl + "/checkout";
        String content = """
            <div style="border-left: 3px solid #EF4444; padding-left: 16px; margin-bottom: 24px;">
                <span style="color: #EF4444; font-size: 12px; text-transform: uppercase; letter-spacing: 2px;">Notice of Transaction Decline</span>
                <h2 style="color: #FFFFFF; font-size: 22px; margin: 4px 0 0 0; font-weight: 600;">Payment Not Processed</h2>
            </div>
            <p style="color: #CCCCCC; font-size: 14px; line-height: 1.6; margin-bottom: 20px;">
                We encountered an issue attempting to settle the payment for your acquisition (Order Reference #%d).
            </p>
            <div style="background-color: #1A1A1A; border: 1px solid rgba(239, 68, 68, 0.3); border-radius: 8px; padding: 18px; margin-bottom: 24px;">
                <p style="margin: 0 0 6px 0; color: #888888; font-size: 12px; text-transform: uppercase;">Decline Reason</p>
                <p style="margin: 0; color: #FCA5A5; font-size: 14px; font-weight: 500;">%s</p>
            </div>
            <p style="color: #AAAAAA; font-size: 13px; line-height: 1.6; margin-bottom: 24px;">
                No funds were debited from your financial institution. If an authorization hold was placed, it will be automatically released according to your bank's policy.
                You may re-attempt payment with another payment instrument.
            </p>
            <p style="text-align: center; margin: 30px 0;">
                <a href="%s" style="background: linear-gradient(135deg, #D4AF37 0%%, #AA771C 100%%); color: #0A0A0A; text-decoration: none; font-weight: 700; font-size: 13px; padding: 12px 28px; border-radius: 6px; display: inline-block; letter-spacing: 1px; text-transform: uppercase;">
                    Retry Payment
                </a>
            </p>
        """.formatted(order.getId(), reason != null ? reason : "Card issuer declined authorization or network timeout", checkoutUrl);

        return wrapInLuxuryLayout("Payment Action Required - Order #" + order.getId(), content);
    }

    public String buildOrderProcessingEmail(Order order) {
        String trackingUrl = frontendUrl + "/account/orders/" + order.getId() + "/track";
        String content = """
            <div style="border-left: 3px solid #3B82F6; padding-left: 16px; margin-bottom: 24px;">
                <span style="color: #3B82F6; font-size: 12px; text-transform: uppercase; letter-spacing: 2px;">In Preparation</span>
                <h2 style="color: #FFFFFF; font-size: 22px; margin: 4px 0 0 0; font-weight: 600;">Consignment Under Handcrafted Preparation</h2>
            </div>
            <p style="color: #CCCCCC; font-size: 14px; line-height: 1.6; margin-bottom: 20px;">
                Your items for Order #%d have advanced to our fulfillment and white-glove packaging atelier.
                Each piece undergoes rigorous authenticity verification and protective packaging prior to dispatch.
            </p>
            <div style="background-color: #1A1A1A; border: 1px solid rgba(212, 175, 55, 0.2); border-radius: 8px; padding: 18px; margin-bottom: 24px;">
                <p style="margin: 0 0 4px 0; color: #888888; font-size: 12px; text-transform: uppercase;">Fulfillment Facility</p>
                <p style="margin: 0; color: #FFFFFF; font-size: 14px;">JPBazaar Central Vault & Logistics Hub</p>
            </div>
            <p style="text-align: center; margin: 30px 0;">
                <a href="%s" style="background: linear-gradient(135deg, #D4AF37 0%%, #AA771C 100%%); color: #0A0A0A; text-decoration: none; font-weight: 700; font-size: 13px; padding: 12px 28px; border-radius: 6px; display: inline-block; letter-spacing: 1px; text-transform: uppercase;">
                    Track Live Progress
                </a>
            </p>
        """.formatted(order.getId(), trackingUrl);

        return wrapInLuxuryLayout("Order In Preparation #" + order.getId(), content);
    }

    public String buildOrderShippedEmail(Order order) {
        String trackingUrl = frontendUrl + "/account/orders/" + order.getId() + "/track";
        String carrier = order.getCarrier() != null ? order.getCarrier() : "JPBazaar White-Glove Express";
        String trackingNum = order.getTrackingNumber() != null ? order.getTrackingNumber() : "JPB-" + order.getId() + "-TRK";
        String estDelivery = order.getEstimatedDeliveryDate() != null
                ? order.getEstimatedDeliveryDate().format(DATE_FORMATTER)
                : "3 - 5 Business Days";

        String content = """
            <div style="border-left: 3px solid #8B5CF6; padding-left: 16px; margin-bottom: 24px;">
                <span style="color: #8B5CF6; font-size: 12px; text-transform: uppercase; letter-spacing: 2px;">Dispatched</span>
                <h2 style="color: #FFFFFF; font-size: 22px; margin: 4px 0 0 0; font-weight: 600;">Your Consignment Is On Its Way</h2>
            </div>
            <p style="color: #CCCCCC; font-size: 14px; line-height: 1.6; margin-bottom: 20px;">
                Order #%d has been safely dispatched into transit. Your package is guarded under specialized courier protocols.
            </p>
            <div style="background-color: #1A1A1A; border: 1px solid rgba(212, 175, 55, 0.25); border-radius: 8px; padding: 20px; margin-bottom: 24px;">
                <table style="width: 100%%; border-collapse: collapse; font-size: 14px;">
                    <tr>
                        <td style="color: #888888; padding: 6px 0;">Logistics Carrier:</td>
                        <td style="color: #FFFFFF; font-weight: 600; text-align: right; padding: 6px 0;">%s</td>
                    </tr>
                    <tr>
                        <td style="color: #888888; padding: 6px 0;">Tracking Number:</td>
                        <td style="color: #F5E084; font-family: monospace; font-weight: 700; text-align: right; padding: 6px 0;">%s</td>
                    </tr>
                    <tr>
                        <td style="color: #888888; padding: 6px 0;">Estimated Delivery:</td>
                        <td style="color: #10B981; font-weight: 600; text-align: right; padding: 6px 0;">%s</td>
                    </tr>
                </table>
            </div>
            <p style="text-align: center; margin: 30px 0;">
                <a href="%s" style="background: linear-gradient(135deg, #D4AF37 0%%, #AA771C 100%%); color: #0A0A0A; text-decoration: none; font-weight: 700; font-size: 13px; padding: 12px 28px; border-radius: 6px; display: inline-block; letter-spacing: 1px; text-transform: uppercase;">
                    Track Consignment
                </a>
            </p>
        """.formatted(order.getId(), carrier, trackingNum, estDelivery, trackingUrl);

        return wrapInLuxuryLayout("Consignment Dispatched - Order #" + order.getId(), content);
    }

    public String buildOutForDeliveryEmail(Order order) {
        String trackingUrl = frontendUrl + "/account/orders/" + order.getId() + "/track";
        String carrier = order.getCarrier() != null ? order.getCarrier() : "JPBazaar Delivery Courier";

        String content = """
            <div style="border-left: 3px solid #F59E0B; padding-left: 16px; margin-bottom: 24px;">
                <span style="color: #F59E0B; font-size: 12px; text-transform: uppercase; letter-spacing: 2px;">Arrival Imminent</span>
                <h2 style="color: #FFFFFF; font-size: 22px; margin: 4px 0 0 0; font-weight: 600;">Out For Delivery Today</h2>
            </div>
            <p style="color: #CCCCCC; font-size: 14px; line-height: 1.6; margin-bottom: 20px;">
                Great news! Your consignment for Order #%d is out with your local courier and scheduled for personal handover today.
            </p>
            <div style="background-color: #1A1A1A; border: 1px solid rgba(212, 175, 55, 0.2); border-radius: 8px; padding: 18px; margin-bottom: 24px;">
                <p style="margin: 0 0 6px 0; color: #888888; font-size: 12px; text-transform: uppercase;">Designated Courier</p>
                <p style="margin: 0; color: #F5E084; font-size: 15px; font-weight: 600;">%s</p>
            </div>
            <p style="color: #AAAAAA; font-size: 13px; line-height: 1.5; margin-bottom: 24px;">
                Please ensure an authorized recipient is present at the destination address to accept the delivery.
            </p>
            <p style="text-align: center; margin: 30px 0;">
                <a href="%s" style="background: linear-gradient(135deg, #D4AF37 0%%, #AA771C 100%%); color: #0A0A0A; text-decoration: none; font-weight: 700; font-size: 13px; padding: 12px 28px; border-radius: 6px; display: inline-block; letter-spacing: 1px; text-transform: uppercase;">
                    Follow Live Courier
                </a>
            </p>
        """.formatted(order.getId(), carrier, trackingUrl);

        return wrapInLuxuryLayout("Out for Delivery Today - Order #" + order.getId(), content);
    }

    public String buildOrderDeliveredEmail(Order order) {
        String trackingUrl = frontendUrl + "/account/orders/" + order.getId() + "/track";
        String content = """
            <div style="border-left: 3px solid #10B981; padding-left: 16px; margin-bottom: 24px;">
                <span style="color: #10B981; font-size: 12px; text-transform: uppercase; letter-spacing: 2px;">Completed Handover</span>
                <h2 style="color: #FFFFFF; font-size: 22px; margin: 4px 0 0 0; font-weight: 600;">Your Package Has Been Delivered</h2>
            </div>
            <p style="color: #CCCCCC; font-size: 14px; line-height: 1.6; margin-bottom: 20px;">
                We are delighted to confirm that Order #%d has reached your destination and was personally handed over.
            </p>
            <div style="background-color: #1A1A1A; border: 1px solid rgba(212, 175, 55, 0.2); border-radius: 8px; padding: 18px; margin-bottom: 24px;">
                <p style="margin: 0; color: #E5E5E5; font-size: 14px; line-height: 1.6;">
                    We trust your newly acquired pieces exceed every expectation. It has been our absolute pleasure catering to your requirements.
                </p>
            </div>
            <p style="text-align: center; margin: 30px 0;">
                <a href="%s" style="background: linear-gradient(135deg, #D4AF37 0%%, #AA771C 100%%); color: #0A0A0A; text-decoration: none; font-weight: 700; font-size: 13px; padding: 12px 28px; border-radius: 6px; display: inline-block; letter-spacing: 1px; text-transform: uppercase;">
                    Review Acquisition
                </a>
            </p>
        """.formatted(order.getId(), trackingUrl);

        return wrapInLuxuryLayout("Delivered - Order #" + order.getId(), content);
    }

    public String buildOrderCancelledEmail(Order order, String reason) {
        String ordersUrl = frontendUrl + "/account/orders";
        String content = """
            <div style="border-left: 3px solid #EF4444; padding-left: 16px; margin-bottom: 24px;">
                <span style="color: #EF4444; font-size: 12px; text-transform: uppercase; letter-spacing: 2px;">Cancellation Notice</span>
                <h2 style="color: #FFFFFF; font-size: 22px; margin: 4px 0 0 0; font-weight: 600;">Order #%d Cancelled</h2>
            </div>
            <p style="color: #CCCCCC; font-size: 14px; line-height: 1.6; margin-bottom: 20px;">
                As per your request or verification parameters, Order #%d has been formally cancelled.
            </p>
            <div style="background-color: #1A1A1A; border: 1px solid rgba(239, 68, 68, 0.3); border-radius: 8px; padding: 18px; margin-bottom: 24px;">
                <p style="margin: 0 0 4px 0; color: #888888; font-size: 12px; text-transform: uppercase;">Cancellation Details</p>
                <p style="margin: 0; color: #FFFFFF; font-size: 14px;">%s</p>
            </div>
            <p style="color: #AAAAAA; font-size: 13px; line-height: 1.5; margin-bottom: 24px;">
                If payment was captured, an automatic refund will be credited back to your original payment method.
            </p>
            <p style="text-align: center; margin: 30px 0;">
                <a href="%s" style="background: #262626; color: #D4AF37; border: 1px solid #D4AF37; text-decoration: none; font-weight: 600; font-size: 13px; padding: 12px 28px; border-radius: 6px; display: inline-block; letter-spacing: 1px; text-transform: uppercase;">
                    Browse Your Account
                </a>
            </p>
        """.formatted(order.getId(), order.getId(), reason != null ? reason : "Requested by customer or administrative update", ordersUrl);

        return wrapInLuxuryLayout("Order Cancellation - #" + order.getId(), content);
    }

    public String buildRefundCompletedEmail(Order order, Payment payment) {
        String ordersUrl = frontendUrl + "/account/orders";
        BigDecimal refundAmount = payment != null && payment.getAmount() != null ? payment.getAmount() : order.getTotalAmount();
        String refundRef = payment != null && payment.getTransactionId() != null ? "RFND-" + payment.getTransactionId() : "RFND-" + order.getId();

        String content = """
            <div style="border-left: 3px solid #10B981; padding-left: 16px; margin-bottom: 24px;">
                <span style="color: #10B981; font-size: 12px; text-transform: uppercase; letter-spacing: 2px;">Refund Processed</span>
                <h2 style="color: #FFFFFF; font-size: 22px; margin: 4px 0 0 0; font-weight: 600;">Refund Completed for Order #%d</h2>
            </div>
            <p style="color: #CCCCCC; font-size: 14px; line-height: 1.6; margin-bottom: 20px;">
                Your refund of <strong>₹%s</strong> has been transmitted through our settlement gateway.
            </p>
            <div style="background-color: #1A1A1A; border: 1px solid rgba(212, 175, 55, 0.2); border-radius: 8px; padding: 20px; margin-bottom: 24px;">
                <table style="width: 100%%; border-collapse: collapse; font-size: 14px;">
                    <tr>
                        <td style="color: #888888; padding: 6px 0;">Refund Reference:</td>
                        <td style="color: #FFFFFF; font-weight: 600; text-align: right; padding: 6px 0;">%s</td>
                    </tr>
                    <tr>
                        <td style="color: #888888; padding: 6px 0;">Total Refunded:</td>
                        <td style="color: #10B981; font-weight: 700; font-size: 16px; text-align: right; padding: 6px 0;">₹%s</td>
                    </tr>
                </table>
            </div>
            <p style="color: #AAAAAA; font-size: 13px; line-height: 1.5; margin-bottom: 24px;">
                Depending on your banking institution, funds should reflect in your account within 3 to 7 business days.
            </p>
            <p style="text-align: center; margin: 30px 0;">
                <a href="%s" style="background: #262626; color: #D4AF37; border: 1px solid #D4AF37; text-decoration: none; font-weight: 600; font-size: 13px; padding: 12px 28px; border-radius: 6px; display: inline-block; letter-spacing: 1px; text-transform: uppercase;">
                    View Order Archive
                </a>
            </p>
        """.formatted(order.getId(), refundAmount, refundRef, refundAmount, ordersUrl);

        return wrapInLuxuryLayout("Refund Completed - Order #" + order.getId(), content);
    }

    private String renderItemsTable(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style=\"width: 100%; border-collapse: collapse; margin-top: 16px; margin-bottom: 16px;\">");
        sb.append("<thead><tr style=\"border-bottom: 1px solid rgba(212, 175, 55, 0.3); text-align: left;\">");
        sb.append("<th style=\"color: #D4AF37; font-size: 11px; text-transform: uppercase; padding: 8px 4px;\">Item</th>");
        sb.append("<th style=\"color: #D4AF37; font-size: 11px; text-transform: uppercase; padding: 8px 4px; text-align: center;\">Qty</th>");
        sb.append("<th style=\"color: #D4AF37; font-size: 11px; text-transform: uppercase; padding: 8px 4px; text-align: right;\">Price</th>");
        sb.append("</tr></thead><tbody>");

        for (OrderItem item : items) {
            sb.append("<tr style=\"border-bottom: 1px solid #222222;\">");
            sb.append("<td style=\"color: #E5E5E5; font-size: 13px; padding: 10px 4px;\">").append(item.getProductName()).append("</td>");
            sb.append("<td style=\"color: #AAAAAA; font-size: 13px; padding: 10px 4px; text-align: center;\">").append(item.getQuantity()).append("</td>");
            sb.append("<td style=\"color: #D4AF37; font-size: 13px; font-weight: 600; padding: 10px 4px; text-align: right;\">₹").append(item.getSubtotal()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    private String renderAddressBlock(Order order) {
        if (order.getAddress() == null) {
            return "";
        }
        var addr = order.getAddress();
        String recipient = order.getUser() != null
                ? (order.getUser().getFirstName() + " " + order.getUser().getLastName()).trim()
                : "Valued Patron";
        String phone = order.getUser() != null && order.getUser().getPhone() != null
                ? order.getUser().getPhone()
                : "N/A";

        return """
            <div style="background-color: #141414; border: 1px solid #262626; border-radius: 8px; padding: 16px; margin-bottom: 24px;">
                <p style="margin: 0 0 6px 0; color: #888888; font-size: 11px; text-transform: uppercase; letter-spacing: 1px;">Delivery Destination</p>
                <p style="margin: 0; color: #E5E5E5; font-size: 13px; line-height: 1.5;">
                    <strong>%s</strong><br/>
                    %s<br/>
                    %s, %s - %s<br/>
                    Phone: %s
                </p>
            </div>
        """.formatted(
                recipient,
                addr.getAddressLine() != null ? addr.getAddressLine() : "",
                addr.getCity() != null ? addr.getCity() : "",
                addr.getState() != null ? addr.getState() : "",
                addr.getPostalCode() != null ? addr.getPostalCode() : "",
                phone
        );
    }

    private String wrapInLuxuryLayout(String title, String bodyContent) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
            </head>
            <body style="margin: 0; padding: 0; background-color: #0A0A0A; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #E5E5E5;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #0A0A0A; padding: 32px 12px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #141414; border: 1px solid rgba(212, 175, 55, 0.25); border-radius: 12px; overflow: hidden; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.7); max-width: 600px; width: 100%%;">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(180deg, #1C1917 0%%, #141414 100%%); padding: 32px 24px 24px 24px; text-align: center; border-bottom: 1px solid rgba(212, 175, 55, 0.2);">
                                        <h1 style="margin: 0; font-family: 'Cinzel', Georgia, serif; font-size: 26px; font-weight: 700; letter-spacing: 4px; color: #D4AF37; text-transform: uppercase;">
                                            JPBAZAAR
                                        </h1>
                                        <p style="margin: 6px 0 0 0; font-size: 11px; letter-spacing: 2px; color: #888888; text-transform: uppercase;">
                                            Exclusive Luxury Curation
                                        </p>
                                    </td>
                                </tr>
                                <!-- Main Body -->
                                <tr>
                                    <td style="padding: 32px 28px;">
                                        %s
                                    </td>
                                </tr>
                                <!-- Concierge Footer -->
                                <tr>
                                    <td style="background-color: #0F0F0F; padding: 24px; text-align: center; border-top: 1px solid #222222;">
                                        <p style="margin: 0 0 8px 0; color: #D4AF37; font-size: 11px; letter-spacing: 1px; text-transform: uppercase; font-weight: 600;">
                                            Bespoke Concierge & Client Services
                                        </p>
                                        <p style="margin: 0 0 12px 0; color: #666666; font-size: 12px; line-height: 1.5;">
                                            For bespoke inquiries or logistics coordination, reply directly to this communication or contact our concierge at <a href="mailto:support@jpbazaar.com" style="color: #D4AF37; text-decoration: none;">support@jpbazaar.com</a>.
                                        </p>
                                        <p style="margin: 0; color: #444444; font-size: 11px;">
                                            &copy; 2026 JPBazaar. All rights reserved. Confidential and privileged communication.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.formatted(title, bodyContent);
    }
}
