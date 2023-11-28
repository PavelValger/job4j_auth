package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.dto.PersonsPassword;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

@RestController
@AllArgsConstructor
@RequestMapping("/persons")
public class PersonController {
    private final PersonService persons;
    private BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;

    private void validPerson(Person person) {
        if (person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("Username and password mustn't be empty");
        }
        if (person.getPassword().length() < 3) {
            throw new IllegalArgumentException("Invalid password");
        }
    }

    @GetMapping("/")
    public ResponseEntity<Collection<Person>> findAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .header("Job4jCustomHeader", "job4j")
                .body(persons.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        return persons.findById(id)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person is not found. Please, check requisites."
                ));
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        validPerson(person);
        person.setPassword(encoder.encode(person.getPassword()));
        return persons.save(person)
                .map(p -> new ResponseEntity<>(p, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        validPerson(person);
        person.setPassword(encoder.encode(person.getPassword()));
        var isUpdated = persons.update(person);
        if (isUpdated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/")
    public ResponseEntity<Person> updatePassword(@RequestBody PersonsPassword personsPassword) {
        var responseEntity = persons.findById(personsPassword.getId())
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person is not found. Please, check requisites."
                ));
        persons.updatePersonsPassword(responseEntity.getBody(), personsPassword);
        return responseEntity;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        var isDeleted = persons.deleteById(id);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void exceptionHandler(Exception e,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {{
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
    }
}
