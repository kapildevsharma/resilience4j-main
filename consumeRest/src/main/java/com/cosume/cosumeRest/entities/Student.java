package com.cosume.cosumeRest.entities;


import java.util.HashSet;
import java.util.Set;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// ManytoMany annotation  with Student and Course class
// student table --> id | name | main_course_id

/*CREATE TABLE student (
        id INT PRIMARY KEY,
        name VARCHAR(100)
);*/


@Entity
@Table(name = "student")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    // Main Course (Many Students can have one main course)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "main_course_id") // separate column
    private Course mainCourse;

    // student_course table --> student_id | course_id
    // Many-to-Many Courses
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_course",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"), // Foreign key in join table pointing to Student
            inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id") // Foreign key in join table pointing to Course
    )
    private Set<Course> courses = new HashSet<>();

    /*CREATE TABLE student_course (
        student_id INT,
        course_id INT,
        PRIMARY KEY (student_id, course_id),
        FOREIGN KEY (student_id) REFERENCES student(id),
        FOREIGN KEY (course_id) REFERENCES course(id)
    );*/

}

