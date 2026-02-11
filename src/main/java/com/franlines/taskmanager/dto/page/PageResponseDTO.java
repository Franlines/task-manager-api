package com.franlines.taskmanager.dto.page;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DTO genérico para respuestas paginadas.
 * @param <T> Tipo de los elementos en la página
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {

    /**
     * Contenido de la página actual
     */
    @JsonProperty("content")
    private List<T> content;

    /**
     * Número de página actual (0-indexed)
     */
    @JsonProperty("pageNumber")
    private int pageNumber;

    /**
     * Tamaño de la página (número de elementos por página)
     */
    @JsonProperty("pageSize")
    private int pageSize;

    /**
     * Número total de elementos en todas las páginas
     */
    @JsonProperty("totalElements")
    private long totalElements;

    /**
     * Número total de páginas
     */
    @JsonProperty("totalPages")
    private int totalPages;

    /**
     * Indica si esta es la primera página
     */
    @JsonProperty("first")
    private boolean first;

    /**
     * Indica si esta es la última página
     */
    @JsonProperty("last")
    private boolean last;

    /**
     * Indica si la página está vacía
     */
    @JsonProperty("empty")
    private boolean empty;

    /**
     * Número de elementos en la página actual
     */
    @JsonProperty("numberOfElements")
    private int numberOfElements;

    /**
     * Constructor a partir de un objeto Page de Spring
     */
    public PageResponseDTO(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
        this.numberOfElements = page.getNumberOfElements();
    }

    /**
     * Método de fábrica para crear un PageResponseDTO a partir de un Page
     */
    public static <T> PageResponseDTO<T> of(Page<T> page) {
        return new PageResponseDTO<>(page);
    }

    /**
     * Método de fábrica para crear un PageResponseDTO a partir de un Page,
     * aplicando un mapper a cada elemento
     */
    public static <T, R> PageResponseDTO<R> of(Page<T> page, Function<T, R> mapper) {
        List<R> content = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty(),
                page.getNumberOfElements()
        );
    }

    /**
     * Método de fábrica para crear un PageResponseDTO a partir de una lista
     * (útil para cuando filtramos después de obtener la página)
     */
    public static <T> PageResponseDTO<T> of(Page<?> page, List<T> content) {
        return new PageResponseDTO<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty(),
                content.size()
        );
    }
}