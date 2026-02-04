package com.franlines.taskmanager.persistance.model;

import com.franlines.taskmanager.enums.TaskState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", nullable = false)
    Long id;

    @NotBlank(message = "La tarea debe tener un título")
    @Size(min = 3, max = 20, message = "El título de la tarea debe tener entre 3 y 20 caracteres")
    @Column(name = "title", nullable = false)
    String titulo;

    @Size(min = 3, max = 20, message = "El descripción de la tarea debe tener entre 3 y 20 caracteres")
    @Column(name = "description")
    String descripcion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "principal_user_id")
    User principalUser;

    @ManyToMany
    @JoinTable(
            name = "task_side_users",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    List<User> sideUsers;

    @ManyToOne(optional = false)
    @JoinColumn(name = "workspace_id")
    Workspace workspace;

    @Column(name = "color")
    String color;

    @ManyToMany
    @JoinTable(
            name = "task_tags",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    List<Tag> tags;

    @Column(name = "creation_date", nullable = false, updatable = false)
    LocalDateTime creationDate;

    @Column(name = "updated_date")
    LocalDateTime updatedDate;

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "task_state", nullable = false)
    TaskState taskState;

    @PrePersist
    protected void onCreate(){
        this.creationDate = LocalDateTime.now();
        if (this.taskState == null){
            this.taskState = TaskState.NOT_READY;
        }
    }
}
