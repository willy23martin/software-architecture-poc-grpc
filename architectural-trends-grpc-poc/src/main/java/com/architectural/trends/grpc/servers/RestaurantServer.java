package com.architectural.trends.grpc.servers;

import com.architectural.trends.grpc.services.RestaurantServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestaurantServer {

    private static final Logger logger = Logger.getLogger(RestaurantServer.class.getName());
    private Server server;

    /**
     * Hosts UserServiceImpl in a port:
     */
    public void startServer() {
        int port = 50052;
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new RestaurantServiceImpl())
                    .build()
                    .start();
            logger.info( "RestaurantServer started.");

            // In case JVM is killed, then we need to ensure we stop the server.
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    logger.info( "RestaurantServer shutdown in case JVM shutdown");
                    try {
                        RestaurantServer.this.stopServer();
                    } catch (InterruptedException exception) {
                        logger.log(Level.SEVERE, "RestaurantServer shutdown interrupted", exception);
                    }
                }
            });
        } catch(IOException exception) {
            logger.log(Level.SEVERE, "cannot start restaurant server", exception);
        }
    }

    public void stopServer() throws InterruptedException {
        if(server!= null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if(server!=null) {
            server.awaitTermination(); // Keep waiting for the termination.
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RestaurantServer restaurantServer = new RestaurantServer();
        restaurantServer.startServer();
        restaurantServer.blockUntilShutdown(); // Keep running until stop server is called.
    }
}
