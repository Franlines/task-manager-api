package com.franlines.taskmanager.persistence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa un espacio de trabajo dentro del sistema.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "workspace")
public class Workspace {
    /**
     * Identificador del espacio de trabajo
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id", nullable = false)
    Long id;

    /**
    * Nombre del espacio de trabajo. Debe ser único
     */
    @NotBlank(message = "El workspace debe tener un nombre")
    @Size(min = 3, max = 20, message = "El nombre del workspace debe tener entre 3 y 20 caracteres")
    @Column(name = "name", nullable = false, unique = true)
    String name;


    /**
     * Descripción del espacio de trabajo. (Opcional)
     */
    @Size(min = 3, max = 100, message = "El descripción del workspace debe tener entre 3 y 100 caracteres")
    @Column(name = "description")
    String descripcion;

    /**
     * Usuario creador del espacio de trabajo.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by_user_id")
    User createdBy;

    /**
     * Fecha en la que se creó el espacio de trabajo.
     * Se asigna automáticamente al persistir la entidad.
     */
    @Column(name = "created_date", nullable = false, updatable = false)
    LocalDateTime createdDate;

    /**
     * Callback JPA que inicializa la fecha de creación.
     */
    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }
}
