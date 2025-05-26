package in.reqres;


import data.reqres.UserData;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class TestPostWithRecords {
    public record UserData( String name, String job) { }
    private static final String URL = "https://reqres.in";

    @Test
    public void testCreateUser() {

        UserData userData = new UserData("Faisal", "QA");

        String response = given ()
                .header("x-api-key","reqres-free-v1")
                .contentType (ContentType.JSON)
                .body (userData)
                .when ()
                .post (URL + "/api/users")
                .then ()
                .assertThat ()
                .statusCode (201).extract().response().asString();

        System.out.println(response);
    }



}
