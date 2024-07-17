package utils;

import dto.RegistrationDto;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserRegistrar {
    public static void registerUser(RequestSpecification requestSpecification,
                                    String serviceEndpoint,
                                    String login,
                                    String password,
                                    String status) {

        given()
                .spec(requestSpecification)
                .body(new RegistrationDto(login, password, status))
                .when()
                .post(serviceEndpoint)
                .then()
                .statusCode(200);
    }
}
