package com.franlines.taskmanager.dto.workspace;

import com.franlines.taskmanager.dto.user.UserResponseDTO;
import lombok.Data;

@Data
public class WorkspaceResponseDTO {
    private Long id;
    private String name;
    private String description;
    private UserResponseDTO createdBy;  // Agregar este campo
    private String createdDate;         // Opcional: si quieres mostrar la fecha
}