package com.franlines.taskmanager.dto.task;

import com.franlines.taskmanager.enums.TaskState;

import java.time.LocalDate;
import java.util.List;

public class TaskFilterDTO {
    private Long workspaceId;
    private LocalDate startDate;
    private LocalDate endDate;
    private TaskState taskState;
    private Long userId; // Para filtrar por usuario asignado
    private List<Long> tagIds;
    private String viewType; // "weekly", "daily", "kanban"
}