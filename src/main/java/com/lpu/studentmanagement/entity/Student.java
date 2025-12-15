package com.lpu.studentmanagement.entity;

import javax.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "roll_no", unique = true, nullable = false)
    private int rollNo;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String course;

    @Column(nullable = false)
    private int marks;

    // **FIX: ADD DEFAULT CONSTRUCTOR (REQUIRED BY JPA)**
    public Student() {
        // Default constructor required by JPA/Hibernate
    }

    // Parameterized constructor for creating new students
    public Student(int rollNo, String name, int age, String email, String course, int marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.age = age;
        this.email = email;
        this.course = course;
        this.marks = marks;
    }

    // Getters and Setters (make sure you have all of these)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getRollNo() { return rollNo; }
    public void setRollNo(int rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", rollNo=" + rollNo +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", course='" + course + '\'' +
                ", marks=" + marks +
                '}';
    }
}