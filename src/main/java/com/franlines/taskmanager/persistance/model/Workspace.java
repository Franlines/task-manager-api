package com.franlines.taskmanager.persistance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "workspace")
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id", nullable = false)
    Long id;

    @NotBlank(message = "El workspace debe tener un nombre")
    @Size(min = 3, max = 20, message = "El nombre del workspace debe tener entre 3 y 20 caracteres")
    @Column(name = "name", nullable = false, unique = true)
    String name;


    @Size(min = 3, max = 100, message = "El descripción del workspace debe tener entre 3 y 100 caracteres")
    @Column(name = "description")
    String descripcion;

}
