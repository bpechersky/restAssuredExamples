package com.restfulbooker;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.hamcrest.Matchers.lessThan;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import net.datafaker.Faker;
import org.testng.annotations.BeforeClass;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created By Faisal Khatri on 18-02-2022
 */
public class BaseSetup {

    @BeforeClass
    public void setup() {

        RequestSpecification requestSpecification = new RequestSpecBuilder().setBaseUri(
                        "http://localhost:8080")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();

        ResponseSpecification responseSpecification = new ResponseSpecBuilder().expectResponseTime(lessThan(20000L))
                .build();

        RestAssured.requestSpecification = requestSpecification;
        RestAssured.responseSpecification = responseSpecification;

    }
    public void setupStubs () {
        Faker faker = new Faker();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String checkin = formatter.format(faker.date().past(10, TimeUnit.DAYS));
        String checkout = formatter.format(faker.date().future(5, TimeUnit.DAYS));





        stubFor(post(urlEqualTo("/booking"))
                .withRequestBody(matchingJsonPath("$.firstname", equalTo(firstName)))
                .withRequestBody(matchingJsonPath("$.lastname", equalTo(lastName)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\n" +
                                "  \"bookingid\": 1,\n" +
                                "  \"booking\": {\n" +
                                "    \"firstname\": \"" + firstName + "\",\n" +
                                "    \"lastname\": \"" + lastName + "\",\n" +
                                "    \"totalprice\": 100,\n" +
                                "    \"depositpaid\": true,\n" +
                                "    \"bookingdates\": {\n" +
                                "      \"checkin\": \"" + checkin + "\",\n" +
                                "      \"checkout\": \"" + checkout + "\"\n" +
                                "    },\n" +
                                "    \"additionalneeds\": \"Breakfast\"\n" +
                                "  }\n" +
                                "}")));
    }


}