package com.lee.jetty.restful;

import org.eclipse.jetty.server.Server;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * @Author Lee
 * @Date 2021/3/1
 */
@Test
public class TestCalRestfulServer {
    private Server server;
    private final int port = 8080;
    private final int securityPort = 8445;
    private final String baseUri = "http://127.0.0.1";
    private final String securityBaseUri = "https://127.0.0.1";

    @BeforeTest
    void initJettyServer() throws FileNotFoundException {
        server = new CalRestfulServer().createHttpAndHttpsServer(port, securityPort);
    }

    @BeforeMethod
    void startJettyServer() throws Exception {
        server.start();
    }

    @Test
    void testSquareRoot() {
        given().baseUri(baseUri).port(port).basePath("/cal/squareRoot")
                .param("input", "25")
                .and().get()
                .then().assertThat().body("output", equalTo(5.0F));
    }

    @Test
    void testSquare() {
        given().baseUri(baseUri).port(port).basePath("/cal/square/{input}")
                .pathParam("input", 5)
                .and().get()
                .then().assertThat().body("output", equalTo(25.0F));
    }

    @Test
    void testHttpsSquare() {
        given().relaxedHTTPSValidation()
//                .keyStore(this.getClass().getResource("https.keystore").getFile(), "store123")
                .baseUri(securityBaseUri).port(securityPort).basePath("/cal/square/{input}")
                .pathParam("input", 5)
                .and().get()
                .then().assertThat().body("output", equalTo(25.0F));
    }

    @AfterMethod
    void stopJettyServer() throws Exception {
        server.stop();
    }
}
