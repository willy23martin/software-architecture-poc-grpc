package com.architectural.trends.grpc.microservice.order.adapters.secondary;

import com.architectural.trends.grpc.microservice.order.database.H2DatabaseConnection;
import com.architectural.trends.grpc.microservice.order.domain.Order;
import com.architectural.trends.grpc.microservice.order.ports.secondary.OrderRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderRepositoryImpl implements OrderRepository {
    private static final Logger logger = Logger.getLogger(OrderRepositoryImpl.class.getName());

    @Override
    public List<Order> getOrders(int customerId) {
        List<Order> orders = new ArrayList<>();
        try {
            Connection connection = H2DatabaseConnection.getConnectionToDatabase();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select * from orders where customer_id=?"
            );
            preparedStatement.setInt(1, customerId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Order order = new Order();
                order.setCustomerId(resultSet.getInt("customer_id"));
                order.setProductId(resultSet.getInt("product_id"));
                order.setNumberOfItems(resultSet.getInt("no_of_items"));
                order.setAmount(resultSet.getDouble("total_amount"));
                orders.add(order);
            }
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "sql statement not executed", exception);
        }
        return orders;
    }

    @Override
    public List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        try (Connection connection = H2DatabaseConnection.getConnectionToDatabase()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select * from orders"
            );

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Order order = new Order();
                    order.setCustomerId(resultSet.getInt("customer_id"));
                    order.setProductId(resultSet.getInt("product_id"));
                    order.setNumberOfItems(resultSet.getInt("no_of_items"));
                    order.setAmount(resultSet.getDouble("total_amount"));
                    orders.add(order);
                }
            }

        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "sql statement not executed", exception);
        }
        return orders;
    }

}
