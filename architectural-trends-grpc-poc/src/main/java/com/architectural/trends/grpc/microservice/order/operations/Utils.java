package com.architectural.trends.grpc.microservice.order.operations;

public class Utils {

    public static void simulateProcessingDelay() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
