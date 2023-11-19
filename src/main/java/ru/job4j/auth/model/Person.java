package ru.job4j.auth.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;

import javax.persistence.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "persons")
public class Person {

    @Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Include
    private String login;
    private String password;
}
