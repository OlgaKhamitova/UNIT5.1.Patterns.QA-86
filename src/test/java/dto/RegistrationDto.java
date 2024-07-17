package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public class RegistrationDto {
    private String login;

    private String password;

    private String status;
}
