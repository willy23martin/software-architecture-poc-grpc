package com.architectural.trends.grpc.services;

import com.architectural.trends.grpc.customer.CustomerRequest;
import com.architectural.trends.grpc.customer.CustomerResponse;
import com.architectural.trends.grpc.customer.Gender;
import com.architectural.trends.grpc.customer.CustomerServiceGrpc;
import com.architectural.trends.grpc.clients.RestaurantClient;
import com.architectural.trends.grpc.database.Customer;
import com.architectural.trends.grpc.database.CustomerDAO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerServiceImpl extends CustomerServiceGrpc.CustomerServiceImplBase {
    private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class.getName());
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    public void getCustomerDetails(CustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {
        Customer customer = customerDAO.getDetails(request.getName());

        CustomerResponse customerResponse = buildCustomerResponse(customer);

        responseObserver.onNext(customerResponse); // returns the user response next time the service is called
        responseObserver.onCompleted(); //make sure gRPC call gets completed
    }

    private CustomerResponse buildCustomerResponse(Customer customer) {
        double dishesAmount = getDishesAmount(customer);
        CustomerResponse.Builder customerResponseBuilder = CustomerResponse.newBuilder()
                .setId(customer.getId())
                .setName(customer.getName())
                .setAge(customer.getAge())
                .setGender(Gender.valueOf(customer.getGender()))
                .setDishesAmount(dishesAmount);

        CustomerResponse customerResponse = customerResponseBuilder.build();
        return customerResponse;
    }

    private double getDishesAmount(Customer customer) {
        logger.info("Creating a channel against the RestaurantServer to retrieve the restaurant dishes for a customer.");

        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052")
                .usePlaintext()
                .build();

        RestaurantClient restaurantClient = new RestaurantClient(channel);
        double dishesAmount = restaurantClient.getRestaurantDishesAmount(customer.getId());

        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            logger.log(Level.SEVERE, "RestaurantClient channel shutdown interrupted", exception);
        }
        return dishesAmount;
    }
}
