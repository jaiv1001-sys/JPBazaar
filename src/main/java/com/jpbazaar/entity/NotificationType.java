package com.jpbazaar.entity;

/**
 * Types of asynchronous email notifications dispatched across JPBazaar.
 */
public enum NotificationType {
    REGISTRATION,
    REGISTRATION_CONFIRMATION,
    ORDER_CONFIRMATION,
    PAYMENT_SUCCESS,
    PAYMENT_FAILURE,
    ORDER_PROCESSING,
    ORDER_SHIPPED,
    OUT_FOR_DELIVERY,
    ORDER_DELIVERED,
    ORDER_CANCELLED,
    REFUND_COMPLETED
}
