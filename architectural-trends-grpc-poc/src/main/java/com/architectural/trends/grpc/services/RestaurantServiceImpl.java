package com.architectural.trends.grpc.services;

import com.architectural.trends.grpc.database.RestaurantDish;
import com.architectural.trends.grpc.database.RestaurantDishDAO;
import com.architectural.trends.grpc.restaurant.RestaurantDishesRequest;
import com.architectural.trends.grpc.restaurant.RestaurantDishesResponse;
import com.architectural.trends.grpc.restaurant.RestaurantServiceGrpc;
import com.google.protobuf.util.Timestamps;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RestaurantServiceImpl extends RestaurantServiceGrpc.RestaurantServiceImplBase {
    private static final Logger logger = Logger.getLogger(RestaurantServiceImpl.class.getName());
    private RestaurantDishDAO restaurantDishDAO = new RestaurantDishDAO();

    @Override
    public void getRestaurantDishesForCustomer(RestaurantDishesRequest request, StreamObserver<RestaurantDishesResponse> responseObserver) {
        List<RestaurantDish> restaurantDishes = restaurantDishDAO.getRestaurantDishes(request.getCustomerId());
        logger.info("Retrieve restaurant dishes from the Database and building RestaurantDishesResponse proto objects");
        List<com.architectural.trends.grpc.restaurant.RestaurantDish> restaurantDishesForCustomer = restaurantDishes.stream().map(
                restaurantDish -> com.architectural.trends.grpc.restaurant.RestaurantDish.newBuilder()
                        .setDishId(restaurantDish.getDish_id())
                        .setCustomerId(restaurantDish.getCustomerId())
                        .setNoOfItems(restaurantDish.getNumberOfItems())
                        .setTotalAmount(restaurantDish.getAmount())
                        .setDate(Timestamps.fromMillis(restaurantDish.getDate().getTime()))
                        .build()
        ).collect(Collectors.toList());

        RestaurantDishesResponse restaurantDishesResponse = RestaurantDishesResponse.newBuilder()
                .addAllRestaurantDish(restaurantDishesForCustomer).build();

        responseObserver.onNext(restaurantDishesResponse);
        responseObserver.onCompleted(); //make sure gRPC call gets completed
    }
}
