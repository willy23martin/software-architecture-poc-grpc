package com.architectural.trends.grpc.microservice.order.adapters.primary;

import com.architectural.trends.grpc.microservice.order.adapters.secondary.OrderRepositoryImpl;
import com.architectural.trends.grpc.microservice.order.converters.OrderConverter;
import com.architectural.trends.grpc.microservice.order.domain.Order;
import com.architectural.trends.grpc.order.OrderRequest;
import com.architectural.trends.grpc.order.OrderResponse;
import com.architectural.trends.grpc.order.OrderServiceGrpc;
import com.architectural.trends.grpc.microservice.order.operations.Utils;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private static final Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());

    private OrderRepositoryImpl orderDAO = new OrderRepositoryImpl();

    private OrderConverter orderConverter = Mappers.getMapper(OrderConverter.class);

    // Unary RPC pattern:
    @Override
    public void retrieveOrdersForCustomer(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        List<Order> orders = orderDAO.getOrders(request.getCustomerId());
        logger.info("Retrieve orders from the Database and building OrdersResponse proto objects");

        List<com.architectural.trends.grpc.order.Order> ordersForCustomer = orders.stream().map(
                   order -> orderConverter.convertToOrderResponse(order)
        ).collect(Collectors.toList());

        OrderResponse orderResponse = OrderResponse.newBuilder()
                .addAllOrder(ordersForCustomer).build();

        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted(); //make sure gRPC call gets completed
    }

    // Server Streaming RPC pattern
    @Override
    public void retrieveOrdersForSearchCriterion
            (
            com.google.protobuf.StringValue searchCriterion,
            StreamObserver<com.architectural.trends.grpc.order.OrderResponse> responseObserver
            )
    {
        logger.info("Retrieve orders from the Database ...");
        List<Order> orders = orderDAO.getOrders();

        logger.info("Searching orders with that criterion ...");

        for(Order order: orders) {
            logger.info("Inventory: Order: \n" + order.toString());
            if(order.toString().contains(searchCriterion.getValue())) {

                logger.info("Order that matches the criteria has been found: \n" + order.toString());

                com.architectural.trends.grpc.order.Order mappedOrder = com.architectural.trends.grpc.order.Order.newBuilder()
                        .setCustomerId(order.getCustomerId())
                        .setProductId(order.getProductId())
                        .setNoOfItems(order.getNumberOfItems())
                        .setTotalAmount(order.getAmount())
                        .build();

                OrderResponse orderResponse = OrderResponse.newBuilder()
                        .addOrder(mappedOrder)
                        .build();
                responseObserver.onNext(orderResponse);
                Utils.simulateProcessingDelay();
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<com.architectural.trends.grpc.order.Order> updateOrders
            (
                    StreamObserver<StringValue> responseObserver
            )
    {
        StreamObserver<com.architectural.trends.grpc.order.Order> ordersToUpdateObserver = new StreamObserver<com.architectural.trends.grpc.order.Order>() {
            String ordersStatus = "Orders' status: \n";
            @Override
            public void onNext(com.architectural.trends.grpc.order.Order order) {
                ordersStatus += updateOrder(order, ordersStatus);
            }

            @Override
            public void onError(Throwable throwable) {
                // Not supported yet
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                        StringValue.newBuilder()
                                .setValue(ordersStatus)
                                .build()
                );
                responseObserver.onCompleted();
            }
        };
        return ordersToUpdateObserver;
    }

    private String updateOrder(com.architectural.trends.grpc.order.Order order, String ordersStatus) {
        logger.info("Looking for the order with customerId, productId: " + order.getCustomerId() + "-" + order.getProductId());
        List<Order> orders = orderDAO.getOrders(order.getCustomerId(), order.getProductId());
        Optional<Order> orderFound = orders.stream().findFirst();
        if(orderFound.isPresent()) {
            logger.info("Updating the order with customerId, productId: " + order.getCustomerId() + "-" + order.getProductId());
            ordersStatus += orderDAO.updateOrder(orderFound.get()) ? "Order updated: " + order.getCustomerId() + "-" + order.getProductId() : "Order not updated: " + order.getCustomerId() + "-" + order.getProductId();
            ordersStatus += "\n";
        } else {
            ordersStatus += "Order not found and cannot be updated: " + order.getCustomerId() + "-" + order.getProductId() + "\n";
        }
        return ordersStatus;
    }

}
