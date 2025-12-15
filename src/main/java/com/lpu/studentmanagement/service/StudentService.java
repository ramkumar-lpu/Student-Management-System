


package com.lpu.studentmanagement.service;

import com.lpu.studentmanagement.entity.Student;
import com.lpu.studentmanagement.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        Optional<Student> student = studentRepository.findById(id);
        return student.orElse(null);
    }

    public Student getStudentByRollNo(int rollNo) {
        return studentRepository.findByRollNo(rollNo);
    }

    @Transactional
    public boolean addStudent(Student student) {
        try {
            if (studentRepository.existsByRollNo(student.getRollNo())) {
                return false;
            }
            studentRepository.save(student);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean updateStudentMarks(int rollNo, int marks) {
        try {
            Student student = studentRepository.findByRollNo(rollNo);
            if (student != null) {
                student.setMarks(marks);
                studentRepository.save(student);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean deleteStudent(int rollNo) {
        System.out.println("=== SERVICE: DELETE STUDENT ===");
        System.out.println("Roll No to delete: " + rollNo);

        try {
            Student student = studentRepository.findByRollNo(rollNo);
            if (student != null) {
                System.out.println("Found student: " + student.getName() + " (ID: " + student.getId() + ")");

                // Delete the student
                studentRepository.delete(student);
                System.out.println("Student deleted successfully");

                return true;
            }
            System.out.println("Student not found!");
            return false;
        } catch (Exception e) {
            System.out.println("Exception in deleteStudent: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Student> searchStudentsByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Student> searchStudentsByCourse(String course) {
        return studentRepository.findByCourseContainingIgnoreCase(course);
    }

    public boolean isRollNumberExists(int rollNo) {
        return studentRepository.existsByRollNo(rollNo);
    }

    public Object[] getStatistics() {
        return studentRepository.getStudentStatistics();
    }

    public Student getTopperStudent() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .max((s1, s2) -> Integer.compare(s1.getMarks(), s2.getMarks()))
                .orElse(null);
    }
}