package utils;
import com.github.javafaker.Faker;
import java.util.Locale;

public class UserDataGenerator {
    public static ValidationInfo generateData() {
        Faker faker = new Faker(new Locale("ru"));
        return new ValidationInfo(
                faker.internet().emailAddress(),
                faker.internet().password()
        );
    }
}
