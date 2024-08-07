package com.architectural.trends.grpc.microservice.order.ports.secondary;

import com.architectural.trends.grpc.microservice.order.domain.ECommerceCustomer;

public interface ECommerceCustomerRepository {
    ECommerceCustomer getDetails(String name);
}
