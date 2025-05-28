package com.restfulbooker;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;


public class WireMockTest {

    private WireMockServer wireMockServer;

    @BeforeClass
    public void startServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080));
        wireMockServer.start();

        configureFor("localhost", 8080);

        stubFor(post(urlEqualTo("/booking"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"bookingid\": 123, \"message\": \"Booking created successfully\"}")));

    }

    @Test
    public void testBooking() {
        given()
                .header("Content-Type", "application/json")
                .body("{\"firstname\": \"John\", \"lastname\": \"Doe\"}")
                .when()
                .post("http://localhost:8080/booking")
                .then()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .body("message", equalTo("Booking created successfully"));


    }

    @AfterClass
    public void stopServer() {
        wireMockServer.stop();
    }
}

