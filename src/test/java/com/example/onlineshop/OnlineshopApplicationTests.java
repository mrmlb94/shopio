package com.example.onlineshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest(properties = "spring.data.mongodb.uri=mongodb://localhost:27017/your-db-name")
public class OnlineshopApplicationTests {

    @Test
    void contextLoads() {}
}
