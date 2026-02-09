package com.franlines.taskmanager.dto.error;

import lombok.Setter;

import java.util.Map;

@Setter
public class ValidationErrorResponseDTO {
    private Map<String, String> errors;
}