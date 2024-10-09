package com.architectural.trends.grpc.microservice.order.adapters.primary;

import com.architectural.trends.grpc.customer.*;
import com.architectural.trends.grpc.microservice.order.clients.ECommerceCustomer;
import com.architectural.trends.grpc.microservice.order.adapters.secondary.ECommerceCustomerRepositoryImpl;
import com.architectural.trends.grpc.microservice.order.converters.OrderConverter;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.mapstruct.factory.Mappers;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ECommerceCustomerServiceImpl extends CustomerServiceGrpc.CustomerServiceImplBase {

    private static final Logger logger = Logger.getLogger(ECommerceCustomerServiceImpl.class.getName());

    private final ECommerceCustomerRepositoryImpl customerDAO = new ECommerceCustomerRepositoryImpl();

    private OrderConverter orderConverter = Mappers.getMapper(OrderConverter.class);

    @Override
    public void getCustomerDetails(CustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {
        com.architectural.trends.grpc.microservice.order.domain.ECommerceCustomer customer = customerDAO.getDetails(request.getName());

        CustomerResponse customerResponse = buildCustomerResponse(customer);

        responseObserver.onNext(customerResponse); // returns the user response next time the service is called
        responseObserver.onCompleted(); //make sure gRPC call gets completed
    }

    @Override
    public void getCustomerOrders(
            com.google.protobuf.StringValue searchCriterion,
            StreamObserver<Empty> responseObserver
    ) {
        /**
         * Server Streaming RPC Call
         */
        retrieveOrdersForSearchCriterion(orderConverter.convertToString(searchCriterion));
        responseObserver.onCompleted();

        /**
         * Client Streaming RPC Call
         */
        updateOrders();

    }

    private CustomerResponse buildCustomerResponse(com.architectural.trends.grpc.microservice.order.domain.ECommerceCustomer customer) {
        /**
         * Unary RPC Call
         */
        double ordersAmount = getOrdersAmount(customer);

        CustomerResponse.Builder customerResponseBuilder = CustomerResponse.newBuilder()
                .setId(customer.getId())
                .setName(customer.getName())
                .setAge(customer.getAge())
                .setGender(Gender.valueOf(customer.getGender()))
                .setOrdersAmount(ordersAmount);

        CustomerResponse customerResponse = customerResponseBuilder.build();
        return customerResponse;
    }

    private double getOrdersAmount(com.architectural.trends.grpc.microservice.order.domain.ECommerceCustomer customer) {
        logger.info("Creating a channel against the OrderServer to retrieve the orders for a customer.");

        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052")
                .usePlaintext()
                .build();

        ECommerceCustomer ordersClient = new ECommerceCustomer(channel);
        double dishesAmount = ordersClient.getOrdersAmount(customer.getId());

        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            logger.log(Level.SEVERE, "ECommerceCustomerClient channel shutdown interrupted", exception);
        }
        return dishesAmount;
    }

    private void retrieveOrdersForSearchCriterion(String searchCriterion) {
        logger.info("Creating a channel against the OrderServer to retrieve the orders for a customer based on Search Criterion.");

        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052")
                .usePlaintext()
                .build();

        ECommerceCustomer ordersClient = new ECommerceCustomer(channel);
        ordersClient.retrieveOrdersForSearchCriterion(searchCriterion);

        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            logger.log(Level.SEVERE, "ECommerceCustomerClient channel shutdown interrupted", exception);
        }
    }

    private void updateOrders() {
        logger.info("Creating a channel against the OrderServer to update orders");

        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052")
                .usePlaintext()
                .build();

        ECommerceCustomer ordersClient = new ECommerceCustomer(channel);
        ordersClient.updateOrders();

        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            logger.log(Level.SEVERE, "ECommerceCustomerClient channel shutdown interrupted", exception);
        }
    }

}
