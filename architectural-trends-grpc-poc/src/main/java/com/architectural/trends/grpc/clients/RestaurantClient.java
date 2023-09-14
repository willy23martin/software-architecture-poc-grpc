package com.architectural.trends.grpc.clients;

import com.architectural.trends.grpc.restaurant.RestaurantDishesRequest;
import com.architectural.trends.grpc.restaurant.RestaurantDishesResponse;
import com.architectural.trends.grpc.restaurant.RestaurantServiceGrpc;
import io.grpc.Channel;

import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RestaurantClient {
    private static final Logger logger = Logger.getLogger(RestaurantClient.class.getName());
    private RestaurantServiceGrpc.RestaurantServiceBlockingStub restaurantServiceBlockingStub;  // get a stub object

    public RestaurantClient(Channel channel) {
        restaurantServiceBlockingStub = RestaurantServiceGrpc.newBlockingStub(channel);
    }

    public double getRestaurantDishesAmount(int customerId) {
        logger.info("RestaurantDishesClient calling the RestaurantService ...");
        RestaurantDishesRequest restaurantDishesRequest = RestaurantDishesRequest.newBuilder()
                .setCustomerId(customerId)
                .build();
        RestaurantDishesResponse restaurantDishesResponse = restaurantServiceBlockingStub.getRestaurantDishesForCustomer(restaurantDishesRequest);
        double restaurantDishesAmount = restaurantDishesResponse.getRestaurantDishList().stream().
                map(restaurantDish -> restaurantDish.getTotalAmount()
                ).collect(Collectors.summingDouble(Double::doubleValue));
        return restaurantDishesAmount;
    }
}
