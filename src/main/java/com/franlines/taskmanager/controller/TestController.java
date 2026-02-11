package com.franlines.taskmanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "Task Manager API",
                "timestamp", LocalDateTime.now().toString(),
                "message", "¡API funcionando sin SpringDoc!"
        );
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Task Manager API!";
    }
}