
import com.codeborne.selenide.Condition;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.RequestSpecificationGenerator;
import utils.UserRegistrar;
import utils.UserDataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

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
        UserRegistrar.registerUser(requestSpecification, SERVICE_ENDPOINT, LOGIN, PASSWORD, "active");
        UserRegistrar.registerUser(requestSpecification, SERVICE_ENDPOINT, BLOCKED_LOGIN, BLOCKED_PASSWORD, "blocked");
    }

    @Test
    public void testSuccessAuthorization() {
        open(SERVICE_URI);
        $("[data-test-id=login] input").setValue(LOGIN);
        $("[data-test-id=password] input").setValue(PASSWORD);
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").shouldBe(hidden, Duration.ofSeconds(15));
        $(withText("Личный кабинет")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    public void testAuthMustFailWithWrongPassword() {
        open(SERVICE_URI);
        $("[data-test-id=login] input").setValue(LOGIN);
        $("[data-test-id=password] input").setValue(UserDataGenerator.generateData().getPassword());
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=error-notification] .notification__content").shouldHave(Condition.exactText("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    public void testAuthMustFailWithNonExistingUser() {
        open(SERVICE_URI);
        $("[data-test-id=login] input").setValue(UserDataGenerator.generateData().getLogin());
        $("[data-test-id=password] input").setValue(PASSWORD);
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=error-notification] .notification__content").shouldHave(Condition.exactText("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    public void testAuthMustFailForBlockedUser() {
        open(SERVICE_URI);
        $("[data-test-id=login] input").setValue(BLOCKED_LOGIN);
        $("[data-test-id=password] input").setValue(BLOCKED_PASSWORD);
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=error-notification] .notification__content").shouldHave(Condition.exactText("Ошибка! Пользователь заблокирован"));
    }
}