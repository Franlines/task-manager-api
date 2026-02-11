package com.franlines.taskmanager.util;

import org.springframework.data.domain.*;

import java.util.List;

/**
 * Clase de utilidad para operaciones de paginación
 */
public class PaginationUtils {

    /**
     * Aplica paginación manual a una lista
     */
    public static <T> Page<T> paginateList(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        if (start > list.size()) {
            return new PageImpl<>(List.of(), pageable, list.size());
        }

        List<T> pageContent = list.subList(start, end);
        return new PageImpl<>(pageContent, pageable, list.size());
    }

    /**
     * Calcula el número total de páginas
     */
    public static int calculateTotalPages(long totalElements, int pageSize) {
        return (int) Math.ceil((double) totalElements / pageSize);
    }

    /**
     * Valida los parámetros de paginación
     */
    public static void validatePaginationParams(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must be at least 1");
        }
        if (size > 1000) {
            throw new IllegalArgumentException("Page size cannot exceed 1000");
        }
    }

    /**
     * Crea un Pageable con valores por defecto si son nulos
     */
    public static Pageable createPageable(Integer page, Integer size, String sortBy, String direction) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? Math.min(size, 100) : 10; // Máximo 100 por página

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            Sort.Direction sortDirection = (direction != null && direction.equalsIgnoreCase("asc"))
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sort = Sort.by(sortDirection, sortBy);
            return PageRequest.of(pageNumber, pageSize, sort);
        }

        return PageRequest.of(pageNumber, pageSize);
    }
}