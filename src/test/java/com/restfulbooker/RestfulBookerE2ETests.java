package com.restfulbooker;

import static data.restfulbooker.BookingDataBuilder.getBookingData;
import static data.restfulbooker.BookingDataBuilder.getPartialBookingData;
import static data.restfulbooker.TokenBuilder.getToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import data.restfulbooker.*;
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
public class RestfulBookerE2ETests extends BaseSetup {
    private WireMockServer wireMockServer;

    @BeforeClass
    public void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080));
        wireMockServer.start();

        configureFor("localhost", 8080);

        newBooking = BookingDataBuilder.getBookingData();
        dates = newBooking.getBookingdates();
        bookingId = 0;

        stubFor(post(urlEqualTo("/booking"))
                .withRequestBody(matchingJsonPath("$.firstname"))
                .withRequestBody(matchingJsonPath("$.lastname"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                        "bookingid": 123,
                        "booking": {
                            "firstname": "Pearline",
                            "lastname": "Wunsch",
                            "totalprice": 438,
                            "depositpaid": true,
                            "bookingdates": {
                                "checkin": "2025-06-01",
                                "checkout": "2025-06-10"
                            },
                            "additionalneeds": "Breakfast"
                        },
                        "message": "Booking created successfully"
                    }
                """)));

    }

    @AfterClass
    public void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    private BookingData newBooking;
    private String token;
    private static BookingDates dates;
    private static int bookingId;

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


        given()
                .header("Content-Type", "application/json")
                .body("{\"firstname\": \"John\", \"lastname\": \"Doe\"}")
                .when()
                .post("http://localhost:8080/booking")
                .then()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .body("message", equalTo("Booking created successfully"));

        bookingId = given().body(newBooking)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body("bookingid", notNullValue())
                .body("booking.firstname", equalTo("Pearline"), "booking.lastname",
                        equalTo("Wunsch"), "booking.totalprice", equalTo(438),
                        "booking.depositpaid", equalTo(true), "booking.bookingdates.checkin", equalTo("2025-06-01"),
                        "booking.additionalneeds", equalTo("Breakfast"))
                .extract()
                .path("bookingid");


    }

    @Test
    @Description("Example test for retrieving a booking - Get request")
    @Severity(SeverityLevel.CRITICAL)
    @Story("End to End tests using rest-assured")
    @Step("Get a the newly created booking")
    public void getBookingTest() {

        stubFor(get(urlEqualTo("/booking/" + bookingId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"bookingid\": 0,\n" +
                                "  \"booking\": {\n" +
                                "    \"firstname\": \"" + newBooking.getFirstname() + "\",\n" +
                                "    \"lastname\": \"" + newBooking.getLastname() + "\",\n" +
                                "    \"totalprice\": " + newBooking.getTotalprice() + ",\n" +
                                "    \"depositpaid\": " + newBooking.isDepositpaid() + ",\n" +
                                "    \"bookingdates\": {\n" +
                                "      \"checkin\": \"" + dates.getCheckin() + "\",\n" +
                                "      \"checkout\": \"" + dates.getCheckout() + "\"\n" +
                                "    },\n" +
                                "    \"additionalneeds\": \"" + newBooking.getAdditionalneeds() + "\"\n" +
                                "  }\n" +
                                "}")));

        given().get("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body("booking.firstname", equalTo(newBooking.getFirstname()))
                .body("booking.lastname", equalTo(newBooking.getLastname()))
                .body("booking.totalprice", equalTo(newBooking.getTotalprice()))
                .body("booking.depositpaid", equalTo(newBooking.isDepositpaid()))
                .body("booking.bookingdates.checkin", equalTo(newBooking.getBookingdates().getCheckin()))
                .body("booking.bookingdates.checkout", equalTo(newBooking.getBookingdates().getCheckout()))
                .body("booking.additionalneeds", equalTo(newBooking.getAdditionalneeds()));
        System.out.println("pause");

    }

    @Test
    @Description("Example test for updating a booking - Put request")
    @Severity(SeverityLevel.NORMAL)
    @Story("End to End tests using rest-assured")
    @Step("Update the booking")
    public void updateBookingTest() {
        BookingData updatedBooking = getBookingData();
        given().body(updatedBooking)
                .when()
                .header("Cookie", "token=" + token)
                .put("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body("firstname", equalTo(updatedBooking.getFirstname()), "lastname",
                        equalTo(updatedBooking.getLastname()), "totalprice", equalTo(updatedBooking.getTotalprice()),
                        "depositpaid", equalTo(updatedBooking.isDepositpaid()), "bookingdates.checkin", equalTo(
                                updatedBooking.getBookingdates()
                                        .getCheckin()), "bookingdates.checkout", equalTo(updatedBooking.getBookingdates()
                                .getCheckout()), "additionalneeds", equalTo(updatedBooking.getAdditionalneeds()));
    }

    @Test
    @Description("Example test for updating a booking partially- Patch request")
    @Severity(SeverityLevel.NORMAL)
    @Story("End to End tests using rest-assured")
    @Step("Update the booking partially")
    public void updatePartialBookingTest() {
        PartialBookingData partialUpdateBooking = getPartialBookingData();
        given().body(partialUpdateBooking)
                .when()
                .header("Cookie", "token=" + token)
                .patch("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body("firstname", equalTo(partialUpdateBooking.getFirstname()), "totalprice",
                        equalTo(partialUpdateBooking.getTotalprice()));

    }

    @Test
    @Description("Example test for deleting a booking - Delete request")
    @Severity(SeverityLevel.NORMAL)
    @Story("End to End tests using rest-assured")
    @Step("Delete the booking")
    public void deleteBookingTest() {
        given().header("Cookie", "token=" + token)
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201);
    }

    @Test
    @Description("Example test for checking if booking is deleted by retrieving a deleted booking - Get request")
    @Severity(SeverityLevel.NORMAL)
    @Story("End to End tests using rest-assured")
    @Step("Check by retrieving deleted booking")
    public void checkBookingIsDeleted() {
        given().get("/booking/" + bookingId)
                .then()
                .statusCode(404);
    }

    @Description("Example test for fetching token value - Post request")
    @Severity(SeverityLevel.BLOCKER)
    @Story("End to End tests using rest-assured")
    @Step("Generate Token")
    @Test
    public void testTokenGeneration() {
        Tokencreds tokenCreds = getToken();
        token = given().body(tokenCreds)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .assertThat()
                .body("token", is(notNullValue()))
                .extract()
                .path("token").toString();
    }
}