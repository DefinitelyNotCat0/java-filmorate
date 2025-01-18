package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    @Email
    @NotEmpty
    private String email;

    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$",
            message = "Login must be of 4 to 12 length with no special characters")
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;
}
