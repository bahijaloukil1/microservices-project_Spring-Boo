package com.demo.enrollment_service.services;

import com.demo.enrollment_service.clients.StudentClient;
import com.demo.enrollment_service.clients.CourseClient;
import com.demo.enrollment_service.dtos.*;
import com.demo.enrollment_service.entities.Enrollment;
import com.demo.enrollment_service.repositories.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentClient studentClient;

    @Autowired
    private CourseClient courseClient;

    
    public String enrollStudent(EnrollmentRequest request) {
        try {
            System.out.println("=== Début inscription ===");
            System.out.println("CNIE reçu: " + request.getStudentCnie());
            System.out.println("CourseId reçu: " + request.getCourseId());
            
            StudentDTO student = studentClient.getStudentByCnie(request.getStudentCnie());
            if (student == null) {
                System.err.println("Student non trouvé avec CNIE: " + request.getStudentCnie());
                return "Étudiant non trouvé avec CNIE: " + request.getStudentCnie();
            }
            System.out.println("Student trouvé: ID=" + student.getId() + ", CNIE=" + student.getCnie());

            CourseDTO course = courseClient.getCourseById(request.getCourseId());
            if (course == null) {
                System.err.println("Cours non trouvé avec ID: " + request.getCourseId());
                return "Cours non trouvé avec ID: " + request.getCourseId();
            }
            System.out.println("Cours trouvé: " + course.getTitle());
                //* 3 �tudiant dans le cours 
            long currentCount = enrollmentRepository.countByCourseId(request.getCourseId());
            System.out.println("Nombre d'inscriptions actuelles: " + currentCount);
            if (currentCount >= 3) {
                return "Cours complet ! Impossible d'inscrire plus de 3 étudiants.";
            }

            List<Enrollment> existingEnrollments = enrollmentRepository.findByStudentId(student.getId());
            boolean alreadyEnrolled = existingEnrollments.stream()
                    .anyMatch(e -> e.getCourseId().equals(request.getCourseId()));
            if (alreadyEnrolled) {
                return "Cet étudiant est déjà inscrit à ce cours !";
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setStudentId(student.getId());
            enrollment.setCourseId(request.getCourseId());
            enrollment.setEnrollmentDate(LocalDateTime.now());

            enrollmentRepository.save(enrollment);
            System.out.println("=== Inscription réussie ===");
            return "Inscription réussie pour l'étudiant " + student.getCnie() + " au cours " + course.getTitle();
            
        } catch (Exception e) {
            System.err.println("ERREUR: " + e.getMessage());
            e.printStackTrace();
            return "Erreur interne: " + e.getMessage();
        }
    }

    
    public List<EnrollmentResponseDTO> getEnrollmentsByStudentId(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        return enrollments.stream().map(enrollment -> {
            StudentDTO student = studentClient.getStudentById(enrollment.getStudentId());
            CourseDTO course = courseClient.getCourseById(enrollment.getCourseId());

            EnrollmentResponseDTO dto = new EnrollmentResponseDTO();
            dto.setEnrollmentId(enrollment.getId());
            dto.setStudentCnie(student != null ? student.getCnie() : "Inconnu");
            dto.setCourseName(course != null ? course.getTitle() : "Inconnu");
            dto.setDate(enrollment.getEnrollmentDate().toString());

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime limit = enrollment.getEnrollmentDate().plusHours(24);
            dto.setDeletable(now.isBefore(limit));

            return dto;
        }).collect(Collectors.toList());
    }

    
    public List<EnrollmentResponseDTO> getEnrollmentsByStudentCnie(String cnie) {
        StudentDTO student = studentClient.getStudentByCnie(cnie);
        if (student == null) {
            return new ArrayList<>();
        }
        return getEnrollmentsByStudentId(student.getId());
    }

    
    public List<EnrollmentResponseDTO> getEnrollmentsByCourseId(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        
        return enrollments.stream().map(enrollment -> {
            StudentDTO student = studentClient.getStudentById(enrollment.getStudentId());
            CourseDTO course = courseClient.getCourseById(courseId);
            
            EnrollmentResponseDTO dto = new EnrollmentResponseDTO();
            dto.setEnrollmentId(enrollment.getId());
            dto.setStudentId(student != null ? student.getId() : null);
            dto.setStudentCnie(student != null ? student.getCnie() : "Inconnu");
            dto.setStudentName(student != null ? student.getFirstName() + " " + student.getLastName() : "Inconnu");
            dto.setStudentEmail(student != null ? student.getEmail() : "Inconnu");
            dto.setCourseName(course != null ? course.getTitle() : "Inconnu");
            dto.setDate(enrollment.getEnrollmentDate().toString());
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime limit = enrollment.getEnrollmentDate().plusHours(24);
            dto.setDeletable(now.isBefore(limit));
            
            return dto;
        }).collect(Collectors.toList());
    }

   
    public String deleteEnrollment(Long enrollmentId) {
        java.util.Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(enrollmentId);

        if (enrollmentOpt.isEmpty()) {
            return "Inscription non trouvée !";
        }

        Enrollment enrollment = enrollmentOpt.get();
          // 24h pour supprimer l'enrolement 
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = enrollment.getEnrollmentDate().plusHours(24);

        if (now.isAfter(limit)) {
            return "Vous ne pouvez pas supprimer cette inscription. Plus de 24 heures se sont écoulées.";
        }

        enrollmentRepository.deleteById(enrollmentId);
        return "Inscription supprimée avec succès !";
    }
}