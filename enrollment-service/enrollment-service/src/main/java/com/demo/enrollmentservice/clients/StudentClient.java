package com.demo.enrollmentservice.clients;

import com.demo.enrollmentservice.dtos.StudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StudentClient {

    @Autowired
    private WebClient webClient;

    public StudentDTO getStudentByCnie(String cnie) {
        try {
            return webClient.get()
                    .uri("http://localhost:3001/api/students/cnie/" + cnie)
                    .retrieve()
                    .bodyToMono(StudentDTO.class)
                    .block();
        } catch (Exception e) {
            System.err.println("Erreur StudentClient: " + e.getMessage());
            return null;
        }
    }

    public StudentDTO getStudentById(Long id) {
        try {
            return webClient.get()
                    .uri("http://localhost:3001/api/students/" + id)
                    .retrieve()
                    .bodyToMono(StudentDTO.class)
                    .block();
        } catch (Exception e) {
            System.err.println("Erreur StudentClient: " + e.getMessage());
            return null;
        }
    }
}