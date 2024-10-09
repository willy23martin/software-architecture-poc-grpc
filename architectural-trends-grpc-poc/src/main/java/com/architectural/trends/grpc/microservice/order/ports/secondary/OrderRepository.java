package com.architectural.trends.grpc.microservice.order.ports.secondary;

import com.architectural.trends.grpc.microservice.order.domain.Order;

import java.util.List;

public interface OrderRepository {

    List<Order> getOrders(int customerId);

    List<Order> getOrders();

    List<Order> getOrders(int customerId, int productId);

    boolean updateOrder(Order order);
}
