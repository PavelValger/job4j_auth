package ru.job4j.auth.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.dto.PersonsPassword;
import ru.job4j.auth.marker.Operation;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.service.PersonService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/persons")
@Validated
public class PersonController {
    private final PersonService persons;
    private BCryptPasswordEncoder encoder;

    @GetMapping("/")
    public ResponseEntity<Collection<Person>> findAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .header("Job4jCustomHeader", "job4j")
                .body(persons.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@Positive @PathVariable int id) {
        return persons.findById(id)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person is not found. Please, check requisites."
                ));
    }

    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        return persons.save(person)
                .map(p -> new ResponseEntity<>(p, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PutMapping("/")
    @Validated(Operation.OnUpdate.class)
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        var isUpdated = persons.update(person);
        if (isUpdated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/")
    public ResponseEntity<Person> updatePassword(@Valid @RequestBody PersonsPassword personsPassword) {
        var responseEntity = persons.findById(personsPassword.getId())
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person is not found. Please, check requisites."
                ));
        var person = responseEntity.getBody();
        person.setPassword(encoder.encode(personsPassword.getPassword()));
        persons.save(person);
        return responseEntity;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Positive @PathVariable Integer id) {
        var isDeleted = persons.deleteById(id);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
