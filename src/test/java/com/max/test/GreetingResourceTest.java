package com.max.test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest

class GreetingResourceTest {
    @Test
    @Tag("greeting")
    void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("Hello from Quarkus REST"));
    }


    @Test
    @Tag("quick")
    void testProcessListRunOnSubscription(){
        Response response = given()
                .when().get("/hello/process-list-on-subscription")
                .thenReturn();

        assert(response.getStatusCode() == 200);
    }

    @Test
    @Tag("quick")
    void testProcessLessEmitOn(){
        Response response = given()
                .when().get("/hello/process-list-on-subscription")
                .thenReturn();

        assert(response.getStatusCode() == 200);
    }


}