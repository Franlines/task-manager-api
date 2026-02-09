package com.franlines.taskmanager.dto.error;

import lombok.Setter;

import java.time.LocalDateTime;

@Setter
public class ErrorResponseDTO {
    private String message;
    private LocalDateTime timestamp;
    private int status;
}