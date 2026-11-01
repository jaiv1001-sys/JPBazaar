package com.jpbazaar.event;

import com.jpbazaar.entity.Order;
import com.jpbazaar.entity.OrderStatus;

public record OrderStatusChangedEvent(Order order, OrderStatus previousStatus, OrderStatus newStatus, String notes) {
}
