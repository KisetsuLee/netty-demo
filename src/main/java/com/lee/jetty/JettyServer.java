package com.lee.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author Lee
 * @Date 2021/1/10
 */
public class JettyServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new HelloWorld());

        server.start();
        server.join();
    }

    private static class HelloWorld extends AbstractHandler {
        @Override
        public void handle(String target,
                           Request baseRequest,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException, ServletException {
            // Declare response encoding and types
            response.setContentType("text/html; charset=utf-8");

            // Declare response status code
            response.setStatus(HttpServletResponse.SC_OK);

            // Write back response
            response.getWriter().println("<h1>Hello World</h1>");

            // Inform jetty that this request has now been handled
            baseRequest.setHandled(true);
        }

    }
}
