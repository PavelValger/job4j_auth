package ru.job4j.auth.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.auth.dto.PersonsPassword;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
public class SimplePersonService implements PersonService, UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(SimplePersonService.class.getName());
    private final PersonRepository personRepository;

    @Override
    public Collection<Person> findAll() {
        return personRepository.findAll();
    }

    @Override
    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    @Override
    public Optional<Person> save(Person person) {
        try {
            return Optional.of(personRepository.save(person));
        } catch (Exception e) {
            LOG.info("Неудачная попытка сохранения person, Exception in log example", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Person person) {
        if (personRepository.existsById(person.getId())) {
            personRepository.save(person);
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePersonsPassword(Person person, PersonsPassword personsPassword) {
        if (personRepository.existsById(person.getId())) {
            person.setPassword(personsPassword.getPassword());
            personRepository.save(person);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        if (personRepository.existsById(id)) {
            personRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<Person> optionalPerson = personRepository.findByLogin(login);
        if (optionalPerson.isEmpty()) {
            throw new UsernameNotFoundException(login);
        }
        var person = optionalPerson.get();
        return new User(person.getLogin(), person.getPassword(), emptyList());
    }
}
