package com.demo.enrollmentservice.dtos;

public class EnrollmentResponseDTO {
    private Long enrollmentId;
    private Long studentId;
    private String studentCnie;
    private String studentName;
    private String studentEmail;
    private String courseName;
    private String date;
    private boolean deletable;

    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentCnie() { return studentCnie; }
    public void setStudentCnie(String studentCnie) { this.studentCnie = studentCnie; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public boolean isDeletable() { return deletable; }
    public void setDeletable(boolean deletable) { this.deletable = deletable; }
}