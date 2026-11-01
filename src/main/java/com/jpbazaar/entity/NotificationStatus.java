package com.jpbazaar.entity;

/**
 * Delivery status of an email notification for audit and deduplication.
 */
public enum NotificationStatus {
    PENDING,
    SENT,
    FAILED,
    SKIPPED
}
