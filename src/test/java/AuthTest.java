
import dto.RegistrationDto;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.RequestSpecificationGenerator;
import utils.UserDataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;

class AuthTest {
    private static final String BASE_URI = "http://localhost";
    private static final int PORT = 9999;
    private static final String SERVICE_URI = BASE_URI + ":" + PORT;
    private static final String SERVICE_ENDPOINT = "/api/system/users";
    private static final String LOGIN = UserDataGenerator.generateData().getLogin();
    private static final String PASSWORD = UserDataGenerator.generateData().getPassword();
    private static final String BLOCKED_LOGIN = UserDataGenerator.generateData().getLogin();
    private static final String BLOCKED_PASSWORD = UserDataGenerator.generateData().getPassword();

    @BeforeAll
    static void setUpAll() {
        RequestSpecification requestSpecification = RequestSpecificationGenerator.getRequestSpec(BASE_URI, PORT);
        given()
                .spec(requestSpecification)
                .body(new RegistrationDto(LOGIN, PASSWORD, "active"))

                .when()
                .post(SERVICE_ENDPOINT)

                .then()
                .statusCode(200);

        given()
                .spec(requestSpecification)
                .body(new RegistrationDto(BLOCKED_PASSWORD, BLOCKED_PASSWORD, "blocked"))

                .when()
                .post(SERVICE_ENDPOINT)

                .then()
                .statusCode(200);
    }

    @Test
    public void testSuccessAuthorization() {
        open(SERVICE_URI);
        $("[data-test-id=login] input").setValue(LOGIN);
        $("[data-test-id=password] input").setValue(PASSWORD);
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").shouldBe(hidden, Duration.ofSeconds(15));
    }

    @Test
    public void testAuthMustFailWithWrongPassword() {
        open(SERVICE_URI);
        $("[data-test-id=login] input").setValue(LOGIN);
        $("[data-test-id=password] input").setValue(UserDataGenerator.generateData().getPassword());
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    public void testAuthMustFailWithNonExistingUser() {
        open(SERVICE_URI);
        $("[data-test-id=login] input").setValue(UserDataGenerator.generateData().getLogin());
        $("[data-test-id=password] input").setValue(PASSWORD);
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    public void testAuthMustFailForBlockedUser() {
        open(SERVICE_URI);
        $("[data-test-id=login] input").setValue(BLOCKED_LOGIN);
        $("[data-test-id=password] input").setValue(BLOCKED_PASSWORD);
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").shouldBe(visible, Duration.ofSeconds(15));
    }
}