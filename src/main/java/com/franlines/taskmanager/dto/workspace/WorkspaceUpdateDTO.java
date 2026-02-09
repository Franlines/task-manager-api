package com.franlines.taskmanager.dto.workspace;


import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceUpdateDTO {

    @Size(min = 3, max = 20, message = "El nombre del workspace debe tener entre 3 y 20 caracteres")
    private String name;

    @Size(min = 3, max = 100, message = "La descripción del workspace debe tener entre 3 y 100 caracteres")
    private String description;
}