package com.cosume.cosumeRest.service;

import com.cosume.cosumeRest.entities.Person;
import com.cosume.cosumeRest.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository repository;
    private final Logger log = LoggerFactory.getLogger(PersonService.class);

    public PersonService(PersonRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public List<Person> findAll() {
        return repository.findAll();
    }

    public Optional<Person> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Person create(Person person) {
        Objects.requireNonNull(person, "person must not be null");
        log.debug("Creating Person: {}", person);
        return repository.save(person);
    }

    /**
     * Upsert: if person with id exists, update fields, otherwise create new (attempt to set id if possible).
     * Returns UpsertResult containing saved entity and a flag whether it was created.
     */
    @Transactional
    public UpsertResult<Person> upsert(Integer id, Person newPerson) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(newPerson, "newPerson must not be null");

        Optional<Person> existing = repository.findById(id);
        if (existing.isPresent()) {
            Person p = existing.get();
            p.setName(newPerson.getName());
            p.setAge(newPerson.getAge());
            Person saved = repository.save(p);
            log.debug("Updated Person: {}", saved);
            return new UpsertResult<>(saved, false);
        } else {
            try {
                // try to set id if setter exists
                newPerson.getClass().getMethod("setId", Integer.class).invoke(newPerson, id);
            } catch (Exception ignore) {
                // ignore if no setId
            }
            Person created = repository.save(newPerson);
            log.debug("Created Person: {}", created);
            return new UpsertResult<>(created, true);
        }
    }

    @Transactional
    public boolean delete(Integer id) {
        Objects.requireNonNull(id, "id must not be null");
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.debug("Deleted Person id={}", id);
            return true;
        }
        return false;
    }

    public static final class UpsertResult<T> {
        private final T entity;
        private final boolean created;

        public UpsertResult(T entity, boolean created) {
            this.entity = entity;
            this.created = created;
        }

        public T getEntity() {
            return entity;
        }

        public boolean isCreated() {
            return created;
        }
    }
}

