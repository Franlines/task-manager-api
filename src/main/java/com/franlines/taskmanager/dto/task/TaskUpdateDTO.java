package com.franlines.taskmanager.dto.task;

import com.franlines.taskmanager.enums.TaskState;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class TaskUpdateDTO {

    @Size(min = 3, max = 50, message = "El título debe tener entre 3 y 50 caracteres")
    private String title;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    @Positive(message = "El ID del usuario principal debe ser positivo")
    private Long principalUserId;

    private List<@Positive(message = "Los IDs de usuarios deben ser positivos") Long> sideUserIds;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "El color debe estar en formato hexadecimal (#RRGGBB)")
    private String color;

    private LocalDate day;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<@Positive(message = "Los IDs de etiquetas deben ser positivos") Long> tagIds;
    private TaskState taskState;
}