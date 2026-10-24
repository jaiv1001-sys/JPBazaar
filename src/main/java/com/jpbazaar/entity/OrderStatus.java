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
    CANCELLED;

    /**
     * Determines whether transitioning from this status to next status is permitted.
     */
    public boolean canTransitionTo(OrderStatus next) {
        if (this == next) {
            return true;
        }
        return switch (this) {
            case PLACED -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == PROCESSING || next == CANCELLED;
            case PROCESSING -> next == SHIPPED || next == CANCELLED;
            case SHIPPED -> next == OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> next == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }

    /**
     * Checks if the order in this state can still be cancelled.
     */
    public boolean isCancellable() {
        return this == PLACED || this == CONFIRMED || this == PROCESSING;
    }
}
