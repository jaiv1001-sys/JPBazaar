package com.jpbazaar.entity;

/**
 * Order status enumeration for order lifecycle tracking.
 */
public enum OrderStatus {
    PLACED,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}
