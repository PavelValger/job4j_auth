package ru.job4j.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonsPassword {
    private Integer id;
    private String password;
}
