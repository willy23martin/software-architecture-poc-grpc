package com.architectural.trends.grpc.microservice.order.adapters.primary;

import com.architectural.trends.grpc.microservice.order.adapters.secondary.OrderRepositoryImpl;
import com.architectural.trends.grpc.microservice.order.converters.OrderConverter;
import com.architectural.trends.grpc.microservice.order.domain.Order;
import com.architectural.trends.grpc.order.OrderRequest;
import com.architectural.trends.grpc.order.OrderResponse;
import com.architectural.trends.grpc.order.OrderServiceGrpc;
import com.architectural.trends.grpc.microservice.order.operations.Utils;
import io.grpc.stub.StreamObserver;
import org.mapstruct.factory.Mappers;

import java.util.List;
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

}
