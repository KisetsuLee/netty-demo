package com.lee.jetty.restful;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author Lee
 * @Date 2021/2/28
 */
@Path("cal")
public class CalRestfulServer {
    @GET
    @Path("squareRoot") // 平方根
    @Produces(MediaType.APPLICATION_JSON)
    public Result squareRoot(@QueryParam("input") double input) {
        Result result = new Result("Square Root");
        result.setInput(input);
        result.setOutput(Math.sqrt(result.getInput()));
        return result;
    }

    @GET
    @Path("square/{input}") // 平方
    @Produces(MediaType.APPLICATION_JSON)
    public Result square(@PathParam("input") double input) {
        Result result = new Result("Square");
        result.setInput(input);
        result.setOutput(result.getInput() * result.getInput());
        return result;
    }

    @Data
    @NoArgsConstructor
    private static class Result {
        double input;
        double output;
        String action;

        public Result(String action) {
            this.action = action;
        }
    }

    public Server createHttpServer(int port) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server server = new Server(port);
        server.setHandler(context);
        ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitOrder(1);

        servletHolder.setInitParameter("jersey.config.server.provider.classnames", this.getClass().getCanonicalName());
        return server;
    }

    public Server createHttpAndHttpsServer(int httpPort, int httpsPort) throws FileNotFoundException {
        java.nio.file.Path keystorePath = Paths.get("src/main/resources/etc/https.keystore").toAbsolutePath();
        if (!Files.exists(keystorePath)) throw new FileNotFoundException(keystorePath.toString());

        Server server = createHttpServer(httpPort);

        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSecureScheme("https");
        httpConfiguration.setSecurePort(httpsPort);

        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
        http.setPort(httpPort);
        http.setIdleTimeout(30000);

        HttpConfiguration httpsConfig = new HttpConfiguration(httpConfiguration);
        SecureRequestCustomizer src = new SecureRequestCustomizer();
        src.setStsMaxAge(2000);
        src.setStsIncludeSubDomains(true);
        httpsConfig.addCustomizer(src);

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(keystorePath.toString());
        sslContextFactory.setKeyStorePassword("store123");
        sslContextFactory.setKeyManagerPassword("key123");
//        sslContextFactory.setWantClientAuth(true);
//        sslContextFactory.setNeedClientAuth(true);

        ServerConnector https = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(httpsConfig));
        https.setPort(httpsPort);
        https.setIdleTimeout(500000);

        server.setConnectors(new Connector[]{http, https});
        return server;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Server jettyServer = new CalRestfulServer().createHttpAndHttpsServer(8080, 8445);
        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jettyServer.destroy();
        }
    }
}
