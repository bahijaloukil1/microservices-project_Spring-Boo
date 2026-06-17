package com.demo.enrollmentservice.controller;

import com.demo.enrollmentservice.dtos.EnrollmentRequest;
import com.demo.enrollmentservice.dtos.EnrollmentResponseDTO;
import com.demo.enrollmentservice.services.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "http://localhost:3000")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping
    public List<EnrollmentResponseDTO> getAllEnrollments() {
        return enrollmentService.getAllEnrollments();
    }

    @PostMapping
    public String enrollStudent(@RequestBody EnrollmentRequest request) {
        return enrollmentService.enrollStudent(request);
    }

    @GetMapping("/student/{cnie}")
    public List<EnrollmentResponseDTO> getEnrollmentsByStudent(@PathVariable String cnie) {
        return enrollmentService.getEnrollmentsByStudentCnie(cnie);
    }

    @GetMapping("/student/id/{studentId}")
    public List<EnrollmentResponseDTO> getEnrollmentsByStudentId(@PathVariable Long studentId) {
        return enrollmentService.getEnrollmentsByStudentId(studentId);
    }

    @GetMapping("/course/{courseId}")
    public List<EnrollmentResponseDTO> getEnrollmentsByCourse(@PathVariable Long courseId) {
        return enrollmentService.getEnrollmentsByCourseId(courseId);
    }

    @DeleteMapping("/{id}")
    public String deleteEnrollment(@PathVariable Long id) {
        return enrollmentService.deleteEnrollment(id);
    }
}