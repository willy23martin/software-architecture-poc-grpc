package com.architectural.trends.grpc.microservice.order.domain;

public class Order {
    private int productId;
    private int customerId;
    private int numberOfItems;
    private double amount;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Order [customerId=" + customerId + ", productId=" + productId + ", numberOfItems=" + numberOfItems + ", amount=" + amount + "]";
    }

}
