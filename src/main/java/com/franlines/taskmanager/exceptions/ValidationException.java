package com.franlines.taskmanager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para errores de validación de datos.
 * Devuelve HTTP 422 (UNPROCESSABLE ENTITY)
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}