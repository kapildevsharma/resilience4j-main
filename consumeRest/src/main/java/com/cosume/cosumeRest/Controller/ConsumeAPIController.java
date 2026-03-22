package com.cosume.cosumeRest.Controller;

/*Resilience4j Tutorial with Spring Boot
 * Circuit Breaker, Retry, Rate Limiter : 
 * URL : https://www.youtube.com/watch?v=9AXAUlp3DBw
*/ 

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cosume.cosumeRest.entities.Person;
import com.cosume.cosumeRest.repository.PersonRepository;

import jakarta.validation.Valid;

@RestController
public class ConsumeAPIController {

    private final PersonRepository personRepository;
    private final Logger log = LoggerFactory.getLogger(ConsumeAPIController.class);

    // Constructor injection (preferred for testability)
    public ConsumeAPIController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/persons")
    public List<Person> getAllPersons() {
        log.info("getAllPersons called");
        return personRepository.findAll();
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<Person> getPersonWithId(@PathVariable Integer personId) {
        log.info("get person information by id={}", personId);
        return personRepository.findById(personId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/person")
    public ResponseEntity<Person> addPerson(@Valid @RequestBody Person person) {
        Person saved = personRepository.save(person);
        log.info("Saved new person: {}", saved);
        // try to include location header if id is present
        try {
            Object id = saved.getClass().getMethod("getId").invoke(saved);
            return ResponseEntity.created(URI.create("/person/" + id)).body(saved);
        } catch (Exception ex) {
            return ResponseEntity.ok(saved);
        }
    }

    @PutMapping("/person/{personId}")
    public ResponseEntity<Person> updateOrCreatePerson(@Valid @RequestBody Person newPerson, @PathVariable Integer personId) {
        Optional<Person> existing = personRepository.findById(personId);
        if (existing.isPresent()) {
            Person udpatePerson = existing.get();
            udpatePerson.setAge(newPerson.getAge());
            udpatePerson.setName(newPerson.getName());
            Person saved = personRepository.save(udpatePerson);
            log.info("Updated person {}", saved);
            return ResponseEntity.ok(saved);
        } else {
            // create new: try to set id if setter exists, otherwise let repo generate
            try {
                newPerson.getClass().getMethod("setId", Integer.class).invoke(newPerson, personId);
            } catch (Exception ignore) {
                // ignore if no setId method
            }
            Person created = personRepository.save(newPerson);
            log.info("Created person {}", created);
            return ResponseEntity.created(URI.create("/person/" + personId)).body(created);
        }
    }

    @DeleteMapping("/person/{personId}")
    public ResponseEntity<Void> deletePerson(@PathVariable int personId) {
        if (personRepository.existsById(personId)) {
            personRepository.deleteById(personId);
            log.info("Deleted person id={}", personId);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("Person id={} not found for delete", personId);
            return ResponseEntity.notFound().build();
        }
    }
}
