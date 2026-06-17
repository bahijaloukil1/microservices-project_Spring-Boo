package com.demo.enrollmentservice.clients;

import com.demo.enrollmentservice.dtos.CourseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CourseClient {

    @Autowired
    private WebClient webClient;

    public CourseDTO getCourseById(Long id) {
        try {
            return webClient.get()
                    .uri("http://localhost:3002/api/courses/" + id)
                    .retrieve()
                    .bodyToMono(CourseDTO.class)
                    .block();
        } catch (Exception e) {
            System.err.println("Erreur CourseClient: " + e.getMessage());
            return null;
        }
    }
}