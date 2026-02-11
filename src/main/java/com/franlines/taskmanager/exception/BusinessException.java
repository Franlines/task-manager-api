package com.franlines.taskmanager.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para errores de negocio/reglas del dominio.
 * Devuelve HTTP 400 (BAD REQUEST) o 422 (UNPROCESSABLE ENTITY)
 * Ejemplos: nombre duplicado, estado inválido, operación no permitida
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}