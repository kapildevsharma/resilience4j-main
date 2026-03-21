package com.cosume.cosumeRest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cosume.cosumeRest.entities.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {
}

