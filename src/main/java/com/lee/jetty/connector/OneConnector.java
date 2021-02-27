package com.lee.jetty.connector;

import com.lee.jetty.helloworld.HelloHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

/**
 * @Author Lee
 * @Date 2021/2/24
 */
public class OneConnector {
    public static Server createServer(int port) throws Exception {
        // The Server
        Server server = new Server();

        // HTTP connector
        ServerConnector http = new ServerConnector(server);
        http.setHost("localhost");
        http.setPort(port);
        http.setIdleTimeout(30000);

        // Set the connector
        server.addConnector(http);

        // Set a handler
        server.setHandler(new HelloHandler());
        return server;
    }

    public static void main(String[] args) throws Exception {
        Server server = createServer(8080);

        // Start the server
        server.start();
        server.join();
    }
}
