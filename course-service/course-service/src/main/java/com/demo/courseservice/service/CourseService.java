package com.demo.courseservice.service;

import com.demo.courseservice.model.Course;
import com.demo.courseservice.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    // زيادة مادة جديدة
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    // جيب جميع المواد
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // جيب مادة بـ ID
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    // حذف مادة بـ ID
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    // تحديث مادة
    public Course updateCourse(Long id, Course courseDetails) {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            course.setTitle(courseDetails.getTitle());
            course.setDescription(courseDetails.getDescription());
            course.setCredits(courseDetails.getCredits());
            return courseRepository.save(course);
        }
        return null;
    }

    // حساب عدد المواد
    public long getCourseCount() {
        return courseRepository.count();
    }
}