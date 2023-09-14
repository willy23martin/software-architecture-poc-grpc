package com.architectural.trends.grpc.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerDAO {
    private static final Logger logger = Logger.getLogger(CustomerDAO.class.getName());

    public Customer getDetails(String name) {
        Customer customer = new Customer();
        try {
            Connection connection = H2DatabaseConnection.getConnectionToDatabase();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from customers where name=?");
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                customer.setId(resultSet.getInt("id"));
                customer.setName(resultSet.getString("name"));
                customer.setAge(resultSet.getInt("age"));
                customer.setGender(resultSet.getString("gender"));
            }
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "sql statement not executed", exception);
        }
        return customer;
    }
}
