package com.jpbazaar.event;

import com.jpbazaar.entity.Order;
import com.jpbazaar.entity.Payment;

public record PaymentStatusEvent(Order order, Payment payment, boolean isSuccess, String failureReason) {
}
