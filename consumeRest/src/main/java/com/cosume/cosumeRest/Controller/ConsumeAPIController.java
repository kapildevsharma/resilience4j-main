package com.cosume.cosumeRest.Controller;

/*Resilience4j Tutorial with Spring Boot
 * Circuit Breaker, Retry, Rate Limiter : 
 * URL : https://www.youtube.com/watch?v=9AXAUlp3DBw
*/ 

import java.net.URI;
import java.util.List;

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
import com.cosume.cosumeRest.service.PersonService;

import jakarta.validation.Valid;

@RestController
public class ConsumeAPIController {

    private final PersonService personService;
    private final Logger log = LoggerFactory.getLogger(ConsumeAPIController.class);

    // Constructor injection (preferred for testability)
    public ConsumeAPIController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/persons")
    public List<Person> getAllPersons() {
        log.info("getAllPersons called");
        return personService.findAll();
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<Person> getPersonWithId(@PathVariable Integer personId) {
        log.info("get person information by id={}", personId);
        return personService.findById(personId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/person")
    public ResponseEntity<Person> addPerson(@Valid @RequestBody Person person) {
        Person saved = personService.create(person);
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
        PersonService.UpsertResult<Person> result = personService.upsert(personId, newPerson);
        if (result.isCreated()) {
            return ResponseEntity.created(URI.create("/person/" + result.getEntity().getId())).body(result.getEntity());
        } else {
            return ResponseEntity.ok(result.getEntity());
        }
    }

    @DeleteMapping("/person/{personId}")
    public ResponseEntity<Void> deletePerson(@PathVariable int personId) {
        boolean deleted = personService.delete(personId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
