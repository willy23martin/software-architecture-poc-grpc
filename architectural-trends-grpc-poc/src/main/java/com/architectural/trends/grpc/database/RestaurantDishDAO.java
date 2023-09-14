package com.architectural.trends.grpc.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestaurantDishDAO {
    private static final Logger logger = Logger.getLogger(RestaurantDishDAO.class.getName());

    public List<RestaurantDish> getRestaurantDishes(int customerId) {
        List<RestaurantDish> restaurantDishes = new ArrayList<>();
        try {
            Connection connection = H2DatabaseConnection.getConnectionToDatabase();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select * from dishes where customer_id=?"
            );
            preparedStatement.setInt(1, customerId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                RestaurantDish restaurantDish = new RestaurantDish();
                restaurantDish.setCustomerId(resultSet.getInt("customer_id"));
                restaurantDish.setDish_id(resultSet.getInt("dish_id"));
                restaurantDish.setNumberOfItems(resultSet.getInt("no_of_items"));
                restaurantDish.setAmount(resultSet.getDouble("total_amount"));
                restaurantDish.setDate(resultSet.getDate("date"));
                restaurantDishes.add(restaurantDish);
            }
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "sql statement not executed", exception);
        }
        return restaurantDishes;
    }
}
