
package com.lpu.studentmanagement.controller;

import com.lpu.studentmanagement.entity.Student;
import com.lpu.studentmanagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Home page - Show all students
    @GetMapping("/")
    public String home(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        model.addAttribute("totalStudents", students.size());

        // Calculate statistics
        if (!students.isEmpty()) {
            double averageMarks = students.stream().mapToInt(Student::getMarks).average().orElse(0.0);
            int maxMarks = students.stream().mapToInt(Student::getMarks).max().orElse(0);
            int minMarks = students.stream().mapToInt(Student::getMarks).min().orElse(0);
            Student topper = studentService.getTopperStudent();

            model.addAttribute("averageMarks", String.format("%.2f", averageMarks));
            model.addAttribute("maxMarks", maxMarks);
            model.addAttribute("minMarks", minMarks);
            model.addAttribute("topper", topper);
        }

        return "index";
    }

    // Add new student - FIXED VERSION
    @PostMapping("/add")
    public String addStudent(@RequestParam int rollNo,
                             @RequestParam String name,
                             @RequestParam int age,
                             @RequestParam String email,
                             @RequestParam String course,
                             @RequestParam int marks,
                             RedirectAttributes redirectAttributes) {

        // Validate input
        if (rollNo <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Roll number must be positive!");
            return "redirect:/";
        }
        if (age < 15 || age > 60) {
            redirectAttributes.addFlashAttribute("errorMessage", "Age must be between 15 and 60!");
            return "redirect:/";
        }
        if (marks < 0 || marks > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "Marks must be between 0 and 100!");
            return "redirect:/";
        }
        if (!email.contains("@")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please enter a valid email address!");
            return "redirect:/";
        }

        Student student = new Student(rollNo, name, age, email, course, marks);

        if (studentService.addStudent(student)) {
            redirectAttributes.addFlashAttribute("successMessage", "✅ Student added successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Roll number " + rollNo + " already exists!");
        }
        return "redirect:/";
    }

    // Update student marks
    @PostMapping("/update-marks")
    public String updateMarks(@RequestParam int rollNo, @RequestParam int marks, RedirectAttributes redirectAttributes) {
        if (marks < 0 || marks > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Marks must be between 0 and 100!");
        } else if (studentService.updateStudentMarks(rollNo, marks)) {
            redirectAttributes.addFlashAttribute("successMessage", "✅ Marks updated for Roll No: " + rollNo);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Student with Roll No " + rollNo + " not found!");
        }
        return "redirect:/";
    }

    // Delete student
    @PostMapping("/delete")
    public String deleteStudent(@RequestParam int rollNo, RedirectAttributes redirectAttributes) {
        if (studentService.deleteStudent(rollNo)) {
            redirectAttributes.addFlashAttribute("successMessage", "✅ Student with Roll No " + rollNo + " deleted!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Failed to delete student!");
        }
        return "redirect:/";
    }

    // Search page
    @GetMapping("/search")
    public String searchPage(Model model) {
        model.addAttribute("searchResults", null);
        return "search";
    }

    // Search students
    @PostMapping("/search")
    public String searchStudents(@RequestParam String searchType,
                                 @RequestParam String keyword,
                                 Model model) {
        List<Student> results = List.of();
        String message = "";

        if (keyword == null || keyword.trim().isEmpty()) {
            message = "❌ Please enter a search keyword!";
        } else {
            switch (searchType) {
                case "rollNo":
                    try {
                        int rollNo = Integer.parseInt(keyword);
                        Student student = studentService.getStudentByRollNo(rollNo);
                        if (student != null) {
                            results = List.of(student);
                            message = "✅ Found student with Roll No: " + rollNo;
                        } else {
                            message = "❌ No student found with Roll No: " + rollNo;
                        }
                    } catch (NumberFormatException e) {
                        message = "❌ Please enter a valid roll number!";
                    }
                    break;

                case "name":
                    results = studentService.searchStudentsByName(keyword);
                    message = results.isEmpty() ?
                            "❌ No students found with name: " + keyword :
                            "✅ Found " + results.size() + " student(s)";
                    break;

                case "course":
                    results = studentService.searchStudentsByCourse(keyword);
                    message = results.isEmpty() ?
                            "❌ No students found in course: " + keyword :
                            "✅ Found " + results.size() + " student(s)";
                    break;
            }
        }

        model.addAttribute("searchResults", results);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("message", message);
        return "search";
    }

    // Generate ID Card
    @GetMapping("/id-card/{rollNo}")
    public String generateIdCard(@PathVariable int rollNo, Model model) {
        Student student = studentService.getStudentByRollNo(rollNo);
        if (student != null) {
            model.addAttribute("student", student);
            return "id-card";
        } else {
            return "redirect:/";
        }
    }
    // Add this method for debugging
    @GetMapping("/debug")
    @ResponseBody
    public String debugInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Debug Information</h1>");

        try {
            // Test database connection
            List<Student> students = studentService.getAllStudents();
            sb.append("<p>Total Students: ").append(students.size()).append("</p>");

            // Test repository methods
            sb.append("<h2>Repository Test:</h2>");
            if (!students.isEmpty()) {
                Student s = students.get(0);
                sb.append("<p>First Student: ").append(s.getName())
                        .append(" (Roll: ").append(s.getRollNo()).append(")</p>");

                // Test delete method
                sb.append("<p><a href='/test-delete/").append(s.getRollNo())
                        .append("'>Test Delete this student</a></p>");
            }

        } catch (Exception e) {
            sb.append("<p style='color:red'>Error: ").append(e.getMessage()).append("</p>");
        }

        return sb.toString();
    }

    @GetMapping("/test-delete/{rollNo}")
    @ResponseBody
    public String testDelete(@PathVariable int rollNo) {
        try {
            // Try to delete
            boolean result = studentService.deleteStudent(rollNo);

            return "<h2>Delete Test Result</h2>" +
                    "<p>Roll No: " + rollNo + "</p>" +
                    "<p>Result: " + (result ? "SUCCESS" : "FAILED") + "</p>" +
                    "<p><a href='/'>Go Back Home</a></p>";
        } catch (Exception e) {
            return "<h2>Error</h2><pre>" + e.getMessage() + "</pre>";
        }
    }
    // View Statistics
    @GetMapping("/statistics")
    public String viewStatistics(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("totalStudents", students.size());

        if (!students.isEmpty()) {
            double averageMarks = students.stream().mapToInt(Student::getMarks).average().orElse(0.0);
            int maxMarks = students.stream().mapToInt(Student::getMarks).max().orElse(0);
            int minMarks = students.stream().mapToInt(Student::getMarks).min().orElse(0);

            model.addAttribute("averageMarks", String.format("%.2f", averageMarks));
            model.addAttribute("maxMarks", maxMarks);
            model.addAttribute("minMarks", minMarks);
            model.addAttribute("topper", studentService.getTopperStudent());
        }

        return "statistics";
    }
}



