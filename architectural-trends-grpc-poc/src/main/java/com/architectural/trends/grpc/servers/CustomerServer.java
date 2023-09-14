package com.architectural.trends.grpc.servers;

import com.architectural.trends.grpc.services.CustomerServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerServer {

    private static final Logger logger = Logger.getLogger(CustomerServer.class.getName());
    private Server server;

    /**
     * Hosts CustomerServiceImpl in a port:
     */
    public void startServer() {
        int port = 50051;
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new CustomerServiceImpl())
                    .build()
                    .start();
            logger.info( "CustomerServer started.");

            // In case JVM is killed, then we need to ensure we stop the server.
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    logger.info( "CustomerServer shutdown in case JVM shutdown");
                    try {
                        CustomerServer.this.stopServer();
                    } catch (InterruptedException exception) {
                        logger.log(Level.SEVERE, "CustomerServer shutdown interrupted", exception);
                    }
                }
            });
        } catch(IOException exception) {
            logger.log(Level.SEVERE, "cannot start customer server", exception);
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
        CustomerServer customerServer = new CustomerServer();
        customerServer.startServer();
        customerServer.blockUntilShutdown(); // Keep running until stop server is called.
    }
}
