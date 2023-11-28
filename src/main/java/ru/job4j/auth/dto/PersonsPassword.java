package ru.job4j.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
public class PersonsPassword {

    @Positive(message = "Id must be Positive")
    private int id;

    @Pattern(regexp = ".{3,8}", message = "The password must contain from 3 to 8 characters")
    private String password;
}
