package com.demo.enrollmentservice.services;

import com.demo.enrollmentservice.clients.StudentClient;
import com.demo.enrollmentservice.clients.CourseClient;
import com.demo.enrollmentservice.dtos.EnrollmentRequest;
import com.demo.enrollmentservice.dtos.EnrollmentResponseDTO;
import com.demo.enrollmentservice.dtos.StudentDTO;
import com.demo.enrollmentservice.dtos.CourseDTO;
import com.demo.enrollmentservice.entities.Enrollment;
import com.demo.enrollmentservice.repositories.EnrollmentRepository;
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
            StudentDTO student = studentClient.getStudentByCnie(request.getStudentCnie());
            if (student == null) {
                return "Etudiant non trouve avec CNIE: " + request.getStudentCnie();
            }

            CourseDTO course = courseClient.getCourseById(request.getCourseId());
            if (course == null) {
                return "Cours non trouve avec ID: " + request.getCourseId();
            }

            long currentCount = enrollmentRepository.countByCourseId(request.getCourseId());
            if (currentCount >= 3) {
                return "Cours complet ! Impossible d'inscrire plus de 3 etudiants.";
            }

            List<Enrollment> existingEnrollments = enrollmentRepository.findByStudentId(student.getId());
            boolean alreadyEnrolled = existingEnrollments.stream()
                    .anyMatch(e -> e.getCourseId().equals(request.getCourseId()));
            if (alreadyEnrolled) {
                return "Cet etudiant est deja inscrit a ce cours !";
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setStudentId(student.getId());
            enrollment.setCourseId(request.getCourseId());
            enrollment.setEnrollmentDate(LocalDateTime.now());

            enrollmentRepository.save(enrollment);
            return "Inscription reussie pour l'etudiant " + student.getCnie() + " au cours " + course.getTitle();

        } catch (Exception e) {
            return "Erreur interne: " + e.getMessage();
        }
    }

    public List<EnrollmentResponseDTO> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();

        return enrollments.stream().map(enrollment -> {
            StudentDTO student = studentClient.getStudentById(enrollment.getStudentId());
            CourseDTO course = courseClient.getCourseById(enrollment.getCourseId());

            EnrollmentResponseDTO dto = new EnrollmentResponseDTO();
            dto.setEnrollmentId(enrollment.getId());
            dto.setStudentId(enrollment.getStudentId());
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

    public List<EnrollmentResponseDTO> getEnrollmentsByStudentId(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        return enrollments.stream().map(enrollment -> {
            StudentDTO student = studentClient.getStudentById(enrollment.getStudentId());
            CourseDTO course = courseClient.getCourseById(enrollment.getCourseId());

            EnrollmentResponseDTO dto = new EnrollmentResponseDTO();
            dto.setEnrollmentId(enrollment.getId());
            dto.setStudentId(enrollment.getStudentId());
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
            return "Inscription non trouvee !";
        }

        Enrollment enrollment = enrollmentOpt.get();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = enrollment.getEnrollmentDate().plusHours(24);

        if (now.isAfter(limit)) {
            return "Vous ne pouvez pas supprimer cette inscription. Plus de 24 heures se sont ecoulees.";
        }

        enrollmentRepository.deleteById(enrollmentId);
        return "Inscription supprimee avec succes !";
    }
}