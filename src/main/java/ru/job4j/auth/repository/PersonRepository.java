package ru.job4j.auth.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.auth.model.Person;

import java.util.Collection;
import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Integer> {

    @Override
    Collection<Person> findAll();

    Optional<Person> findByLogin(String login);
}
