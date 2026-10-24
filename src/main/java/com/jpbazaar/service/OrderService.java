package com.jpbazaar.service;

import com.jpbazaar.dto.request.CreateOrderRequest;
import com.jpbazaar.dto.request.UpdateOrderStatusRequest;
import com.jpbazaar.dto.response.OrderResponse;
import com.jpbazaar.dto.response.PageResponse;
import com.jpbazaar.entity.OrderStatus;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse placeOrder(String userEmail, CreateOrderRequest request);

    OrderResponse getOrderById(String userEmail, Long orderId, boolean isAdmin);

    PageResponse<OrderResponse> getUserOrders(String userEmail, OrderStatus status, Pageable pageable);

    PageResponse<OrderResponse> getAllOrders(OrderStatus status, Pageable pageable);

    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    OrderResponse cancelOrder(String userEmail, Long orderId, boolean isAdmin);
}
