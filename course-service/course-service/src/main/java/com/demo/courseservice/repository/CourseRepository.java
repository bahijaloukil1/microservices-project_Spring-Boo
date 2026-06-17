package com.demo.courseservice.repository;

import com.demo.courseservice.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // يمكنك إضافة دوال بحث خاصة إذا احتجتها
}