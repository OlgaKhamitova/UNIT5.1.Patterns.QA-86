package utils;
import lombok.Getter;


@Getter
public class ValidationInfo {
    private final String login;
    private final String password;


    public ValidationInfo(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
