package com.franlines.taskmanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Task Manager API");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

    @GetMapping("/info")
    public Map<String, String> apiInfo() {
        return Map.of(
                "name", "Task Manager API",
                "description", "API para gestión colaborativa de tareas"
        );
    }
}