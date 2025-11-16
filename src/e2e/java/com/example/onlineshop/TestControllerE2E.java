package com.example.onlineshop;

import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestControllerE2E {

    @LocalServerPort
    int port;

    @Test
    void helloEndpointShouldWork() {
        RestAssured
            .given()
            .port(port)
            .when()
            .get("/api/test")
            .then()
            .statusCode(200)
            .body(equalTo("ok"));
    }
}
