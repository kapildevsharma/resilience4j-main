package com.cosume.cosumeRest.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//ManytoMany annotation  with Student and Course class
// course table --> id | name

/*CREATE TABLE course (
        id INT PRIMARY KEY,
        name VARCHAR(100)
);*/


@Entity
@Table(name = "course")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    // Many-to-Many inverse side
    @ManyToMany(mappedBy = "courses", cascade =  {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();;
    // One-to-Many for mainCourse relationship
    @OneToMany(mappedBy = "mainCourse", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Student> mainStudents = new HashSet<>();

}