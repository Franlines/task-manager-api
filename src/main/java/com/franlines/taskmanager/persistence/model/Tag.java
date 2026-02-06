package com.franlines.taskmanager.persistence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa una etiqueta de las tareas.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag {

    /**
     * Identificador único de la etiqueta
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false)
    Long id;

    /**
     * Nombre único de la etiqueta. (Obligatorio)
     */
    @NotBlank(message = "El nombre del tag no debe estar vacío")
    @Size(max = 20, message = "El nombre del tag debe tener menos de 20 caracteres")
    @Column(name = "tag_name", nullable = false, unique = true)
    String name;

    /**
     * Color de la etiqueta en formato hexadecimal (#RRGGBB).
     */
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Error en el formato de creación del color.")
    String color;

}
