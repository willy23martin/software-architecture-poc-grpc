package com.architectural.trends.grpc.microservice.order.clients;

import com.architectural.trends.grpc.microservice.order.converters.OrderConverter;
import com.architectural.trends.grpc.order.Order;
import com.architectural.trends.grpc.order.OrderRequest;
import com.architectural.trends.grpc.order.OrderResponse;
import com.architectural.trends.grpc.order.OrderServiceGrpc;

import io.grpc.Channel;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ECommerceCustomer {
    private static final Logger logger = Logger.getLogger(ECommerceCustomer.class.getName());

    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub;

    private OrderConverter orderConverter = Mappers.getMapper(OrderConverter.class);

    public ECommerceCustomer(Channel channel) {
        orderServiceBlockingStub = OrderServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Unary RPC Pattern
     */
    public double getOrdersAmount(int customerId) {
        logger.info("OrdersClient calling the OrdersService ...");
        OrderRequest orderRequest = OrderRequest.newBuilder()
                .setCustomerId(customerId)
                .build();
        OrderResponse ordersResponse = orderServiceBlockingStub.retrieveOrdersForCustomer(orderRequest);
        double ordersAmount = ordersResponse.getOrderList().stream().
                map(order -> order.getTotalAmount()
                ).collect(Collectors.summingDouble(Double::doubleValue));
        return ordersAmount;
    }

    /**
     * Server Streaming RPC Pattern
     */
    public void retrieveOrdersForSearchCriterion(String searchCriterion) {
        logger.info("OrdersClient calling the OrdersService for retrieving orders based on search criterion: " + searchCriterion);
        List<Order> ordersFound = new ArrayList<>();
        orderServiceBlockingStub.retrieveOrdersForSearchCriterion(orderConverter.convertToProtobufStringValue(searchCriterion))
                .forEachRemaining(orderResponse -> {
                    orderResponse.getOrderList().stream().forEach(
                            order -> {
                                logger.info("Order retrieved: " + order.toString());
                                ordersFound.add(order);
                            }
                    );
                });
     }

}
