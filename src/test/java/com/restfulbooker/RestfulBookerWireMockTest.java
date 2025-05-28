package com.restfulbooker;

import static data.restfulbooker.BookingDataBuilder.getBookingData;
import static data.restfulbooker.BookingDataBuilder.getPartialBookingData;
import static data.restfulbooker.TokenBuilder.getToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import data.restfulbooker.BookingData;
import data.restfulbooker.PartialBookingData;
import data.restfulbooker.Tokencreds;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import java.io.IOException;

/**
 * Created By Faisal Khatri on 18-02-2022
 */
@Epic("Rest Assured POC - Example Tests")
@Feature("Writing End to End tests using rest-assured")



public class RestfulBookerWireMockTest {

    private WireMockServer wireMockServer;

    @BeforeClass
    public void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080));
        wireMockServer.start();
        configureFor("localhost", 8080);

        wireMockServer.stubFor(post(urlEqualTo("/booking"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"bookingid\": 123,\n" +
                                "  \"booking\": {\n" +
                                "    \"firstname\": \"Jim\",\n" +
                                "    \"lastname\": \"Brown\",\n" +
                                "    \"totalprice\": 111,\n" +
                                "    \"depositpaid\": true,\n" +
                                "    \"bookingdates\": {\n" +
                                "      \"checkin\": \"2023-01-01\",\n" +
                                "      \"checkout\": \"2023-01-02\"\n" +
                                "    },\n" +
                                "    \"additionalneeds\": \"Breakfast\"\n" +
                                "  }\n" +
                                "}")));
    }


    @AfterClass
    public void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
    private BookingData newBooking;
    private int bookingId;
    private String token;

    @BeforeTest
    public void testSetup() {
        newBooking = getBookingData();
    }

    @Test
    @Description("Example test for creating new booking - Post request")
    @Severity(SeverityLevel.BLOCKER)
    @Story("End to End tests using rest-assured")
    @Step("Create new booking")
    public void createBookingTest() throws IOException {


        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080));
        wireMockServer.start();


        given()
                .when()
                .post("http://localhost:8080/booking")
                .then()
                .log().all()
                .statusCode(200)
                .body("bookingid", notNullValue());




    }

}
