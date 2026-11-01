package com.jpbazaar.event;

import com.jpbazaar.entity.Order;

public record OrderPlacedEvent(Order order) {
}
