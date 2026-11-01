package com.jpbazaar.event;

import com.jpbazaar.entity.Order;
import com.jpbazaar.entity.Payment;

public record RefundCompletedEvent(Order order, Payment payment) {
}
