package com.architectural.trends.grpc.microservice.order.converters;

import com.architectural.trends.grpc.microservice.order.domain.Order;
import com.google.protobuf.StringValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OrderConverter {

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "noOfItems", source = "numberOfItems")
    @Mapping(target = "totalAmount", source = "amount")
    com.architectural.trends.grpc.order.Order convertToOrderResponse(Order order);

    StringValue convertToProtobufStringValue(String searchCriterion);

    String convertToString(com.google.protobuf.StringValue searchCriterion);

}
