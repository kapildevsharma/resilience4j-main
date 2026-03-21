package com.cosume.cosumeRest.Controller;

/*Resilience4j Tutorial with Spring Boot
 * Circuit Breaker, Retry, Rate Limiter : 
 * URL : https://www.youtube.com/watch?v=9AXAUlp3DBw
*/ 

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private PersonRepository personRepository;
	
	@GetMapping("/persons")
	public List<Person> getAllPersons() {
		System.out.println("RetryServcie called");
		System.out.println("get all persone information");
		return personRepository.findAll();
	}

	@GetMapping("/person/{personId}")
	public Person getPersonWithId(@Valid @PathVariable Integer personId) {
		System.out.println("get person informatino by person id" + personId);
		return personRepository.findById(personId).get();
	}

	@PostMapping("/person")
	public void addPerson(@Valid @RequestBody Person person) {
		personRepository.save(person);
		System.out.println("Saving new person information" + person);
	}

	@PutMapping("/person/{personId}")
	public void updateOrCreatePerson(@RequestBody Person newPerson, @PathVariable Integer personId) {
		Optional<Person> person = personRepository.findById(personId);
		if (person.isPresent()) {
			Person udpatePerson = person.get();
			udpatePerson.setAge(newPerson.getAge());
			udpatePerson.setName(newPerson.getName());
			personRepository.save(udpatePerson);
		} else {
			System.out.println("Please check the person id" + personId);
		}
		System.out.println("Updating person information");
	}

	@DeleteMapping("/person/{personId}")
	public void addPerson(@Valid @PathVariable int personId) {
		personRepository.deleteById(personId);
		System.out.println("delete person information");
	}
}
