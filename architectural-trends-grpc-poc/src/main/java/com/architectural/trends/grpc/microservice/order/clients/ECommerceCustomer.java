package com.architectural.trends.grpc.microservice.order.clients;

import com.architectural.trends.grpc.microservice.order.converters.OrderConverter;
import com.architectural.trends.grpc.order.Order;
import com.architectural.trends.grpc.order.OrderRequest;
import com.architectural.trends.grpc.order.OrderResponse;
import com.architectural.trends.grpc.order.OrderServiceGrpc;

import com.google.protobuf.StringValue;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ECommerceCustomer {
    private static final Logger logger = Logger.getLogger(ECommerceCustomer.class.getName());

    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub;

    private OrderServiceGrpc.OrderServiceStub asyncOrderServiceStub;

    private OrderConverter orderConverter = Mappers.getMapper(OrderConverter.class);

    public ECommerceCustomer(Channel channel) {
        orderServiceBlockingStub = OrderServiceGrpc.newBlockingStub(channel);
        asyncOrderServiceStub = OrderServiceGrpc.newStub(channel);
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

    /**
     * Client Streaming RPC Pattern
     */
    public void updateOrders() {
        logger.info("OrdersClient calling the OrdersService for updating orders ... ");
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<Order> requestObserver = asyncOrderServiceStub.updateOrders(new StreamObserver<StringValue>() {
            @Override
            public void onNext(StringValue stringValue) {
                logger.info("Orders status: " + stringValue.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                logger.severe("Error updating orders: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                logger.info("Order service completed");
                latch.countDown();
            }
        });

        logger.info("Sending first order to get updated ...");
        requestObserver.onNext(
                Order.newBuilder()
                        .setCustomerId(1)
                        .setProductId(1)
                        .setNoOfItems(4)
                        .setTotalAmount(2500)
                        .build()
        );

        logger.info("Sending second order to get updated ...");
        requestObserver.onNext(
                Order.newBuilder()
                        .setCustomerId(3)
                        .setProductId(2)
                        .setNoOfItems(4)
                        .setTotalAmount(2500)
                        .build()
        );

        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
