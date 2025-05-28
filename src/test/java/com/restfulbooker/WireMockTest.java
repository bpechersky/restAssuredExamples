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

        stubFor(get(urlEqualTo("/booking"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"bookingid\": 123, \"message\":\"Booking fetched successfully\"}")));
    }

    @Test
    public void testBooking() {
        given()
                .when()
                .get("http://localhost:8080/booking")
                .then()
                .log().all()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .body("bookingid", equalTo(123))
                .body("message", equalTo("Booking fetched successfully"));

//
    }

    @AfterClass
    public void stopServer() {
        wireMockServer.stop();
    }
}

