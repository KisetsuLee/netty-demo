package com.lee.jetty.restful;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @Author Lee
 * @Date 2021/2/28
 */
@Path("cal")
public class CalRestfulServer {
    @GET
    @Path("squareRoot")
    @Produces(MediaType.APPLICATION_JSON)
    public Result squareRoot(@QueryParam("input") double input) {
        Result result = new Result("Square Root");
        result.setInput(input);
        result.setOutput(Math.sqrt(result.getInput()));
        return result;
    }

    @GET
    @Path("square")
    @Produces(MediaType.APPLICATION_JSON)
    public Result square(@QueryParam("input") double input) {
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

    public Server createServer(int port) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server server = new Server(port);
        server.setHandler(context);
        ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitOrder(1);

        servletHolder.setInitParameter("jersey.config.server.provider.classnames", this.getClass().getCanonicalName());
        return server;
    }

    public static void main(String[] args) {
        Server jettyServer = new CalRestfulServer().createServer(8888);
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
