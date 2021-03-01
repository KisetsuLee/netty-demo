package com.lee.jetty.restful;

import org.eclipse.jetty.server.Server;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * @Author Lee
 * @Date 2021/3/1
 */
@Test
public class TestCalRestfulServer {
    private Server server;

    @BeforeTest
    void initJettyServer() {
        server = new CalRestfulServer().createServer(8888);
    }

    @BeforeMethod
    void startJettyServer() throws Exception {
        server.start();
    }

    @Test
    void testSquareRoot() {
        given().baseUri("http://127.0.0.1").port(8888).basePath("/cal/squareRoot")
                .param("input", "25")
                .and().get()
//        get("http://127.0.0.1:8888/cal/squareRoot?input=25")
                .then().assertThat().body("output", equalTo(5.0F));
    }

    @Test
    void testSquare() {
        given().baseUri("http://127.0.0.1").port(8888).basePath("/cal/square/{input}")
                .pathParam("input", 5)
                .and().get()
//        get("http://127.0.0.1:8888/cal/square/5")
                .then().assertThat().body("output", equalTo(25.0F));
    }

    @AfterMethod
    void stopJettyServer() throws Exception {
        server.stop();
    }
}
