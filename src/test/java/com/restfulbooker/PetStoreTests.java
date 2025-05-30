package com.restfulbooker;

import io.qameta.allure.*;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class PetStoreTests {

    private int petId;  // to store created pet id if needed later

    @Test
    @Description("Example test for creating a new pet - Post request")
    @Severity(SeverityLevel.BLOCKER)
    @Story("End to End tests using rest-assured")
    @Step("Create new pet")
    public void createPetTest() {

        String newPetJson = """
            {
              "id": 12345,
              "category": {
                "id": 1,
                "name": "Dogs"
              },
              "name": "Rex",
              "photoUrls": [
                "https://example.com/photo1.jpg"
              ],
              "tags": [
                {
                  "id": 1,
                  "name": "friendly"
                }
              ],
              "status": "available"
            }
            """;

        petId =
                given()
                        .header("Content-Type", "application/json")
                        .body(newPetJson)
                        .when()
                        .post("https://petstore.swagger.io/v2/pet")
                        .then()
                        .statusCode(200)
                        .body("id", equalTo(12345))
                        .body("name", equalTo("Rex"))
                        .body("status", equalTo("available"))
                        .extract()
                        .path("id");
    }
}

