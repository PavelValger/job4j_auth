package ru.job4j.auth.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import ru.job4j.auth.marker.Operation;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "persons")
public class Person {

    @Positive(message = "Id must be Positive", groups = {
            Operation.OnUpdate.class
    })
    @Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Login must be not empty", groups = {
            Operation.OnUpdate.class, Operation.OnCreate.class
    })
    @Include
    private String login;

    @Pattern(regexp = ".{3,8}", message = "The password must contain from 3 to 8 characters", groups = {
            Operation.OnUpdate.class, Operation.OnCreate.class
    })
    private String password;
}
