package com.example.onlineshop;



// this test if not a real e2e test , 
// I added it to check if TDD is ready to start or not ! 
// ignored in calculations ... and codcov also
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
