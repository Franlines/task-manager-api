package com.franlines.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando no se encuentra una entidad en la base de datos.
 * Devuelve HTTP 404 (NOT FOUND)
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String resourceName, Long id) {
        super(String.format("%s con ID %d no encontrado", resourceName, id));
    }

    public EntityNotFoundException(String resourceName, String identifier) {
        super(String.format("%s con identificador '%s' no encontrado", resourceName, identifier));
    }
}