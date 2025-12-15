package com.lpu.studentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentManagementWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementWebApplication.class, args);
        System.out.println("🎓 LPU Student Management System Started!");
        System.out.println("🌐 Open: http://localhost:8080");
    }
}