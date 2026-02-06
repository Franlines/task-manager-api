package com.franlines.taskmanager.persistence.model;

import com.franlines.taskmanager.enums.TaskState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *     Entidad que representa a una tarea dentro del sistema
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {

    /**
     * Identificador de la tarea
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", nullable = false)
    Long id;

    /**
     * Título de la tarea. No debe ser único
     */
    @NotBlank(message = "La tarea debe tener un título")
    @Size(min = 3, max = 20, message = "El título de la tarea debe tener entre 3 y 20 caracteres")
    @Column(name = "title", nullable = false)
    String titulo;

    /**
     * Descripción de la tarea. (Opcional)
     */
    @Size(min = 3, max = 20, message = "La descripción de la tarea debe tener entre 3 y 20 caracteres")
    @Column(name = "description")
    String descripcion;


    /**
     * Usuario creador de la tarea.
     * Siempre debe existir y es el responsable principal.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "principal_user_id")
    User principalUser;

    /**
     * Usuarios secundarios asociados a la tarea.
     * Pueden colaborar o tener acceso limitado.
     */
    @ManyToMany
    @JoinTable(
            name = "task_side_users",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    List<User> sideUsers = new ArrayList<>();

    /**
     * Workspace al que pertenece la tarea
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "workspace_id")
    Workspace workspace;

    /**
     * Color de la etiqueta en formato hexadecimal (#RRGGBB).
     */
    @Column(name = "color")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Error en el formato de creación del color.")
    String color;

    /**
     * Lista de etiquetas de la tarea. (Opcional)
     */
    @ManyToMany
    @JoinTable(
            name = "task_tags",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    List<Tag> tags = new ArrayList<>();

    /**
     * Fecha de creación de la tarea.
     * Se asigna automáticamente al crear la entidad.
     */
    @Column(name = "creation_date", nullable = false, updatable = false)
    LocalDateTime creationDate;

    /**
     * Fecha de la última actualización
     * Se asigna automáticamente al actualizar la entidad
     */
    @Column(name = "updated_date")
    LocalDateTime updatedDate;

    /**
     * Estado de la tarea
     * TO_DO, IN_PROGRESS, DONE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "task_state", nullable = false)
    TaskState taskState;

    /**
     * Inicializa los valores por defecto de la tarea antes de persistirla.
     * - Asigna la fecha de creación.
     * - Establece el estado inicial si no se ha definido.
     */
    @PrePersist
    protected void onCreate(){
        this.creationDate = LocalDateTime.now();
        if (this.taskState == null){
            this.taskState = TaskState.TO_DO;
        }
    }

    /**
     * Actualiza la fecha de última modificación antes de actualizar la entidad.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
