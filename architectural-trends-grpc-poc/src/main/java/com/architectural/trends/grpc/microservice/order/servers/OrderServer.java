package com.architectural.trends.grpc.microservice.order.servers;

import com.architectural.trends.grpc.microservice.order.adapters.primary.OrderServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderServer {

    private static final Logger logger = Logger.getLogger(OrderServer.class.getName());
    private Server server;

    /**
     * Hosts UserServiceImpl in a port:
     */
    public void startServer() {
        int port = 50052;
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new OrderServiceImpl())
                    .build()
                    .start();
            logger.info( "OrderServer started.");

            // In case JVM is killed, then we need to ensure we stop the server.
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    logger.info( "OrderServer shutdown in case JVM shutdown");
                    try {
                        OrderServer.this.stopServer();
                    } catch (InterruptedException exception) {
                        logger.log(Level.SEVERE, "OrderServer shutdown interrupted", exception);
                    }
                }
            });
        } catch(IOException exception) {
            logger.log(Level.SEVERE, "cannot start order server", exception);
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
        OrderServer orderServer = new OrderServer();
        orderServer.startServer();
        orderServer.blockUntilShutdown(); // Keep running until stop server is called.
    }
}
