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
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false)
    Long id;

    @NotBlank(message = "El nombre del tag no debe estar vacío")
    @Size(max = 20, message = "El nombre del tag debe tener menos de 20 caracteres")
    @Column(name = "tagname", nullable = false)
    String name;

    //Revisar mejor opción para color
    String color;
}
