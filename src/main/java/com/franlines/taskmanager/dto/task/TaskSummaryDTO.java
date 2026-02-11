package com.franlines.taskmanager.dto.task;

import lombok.Data;
import com.franlines.taskmanager.enums.TaskState;

@Data
public class TaskSummaryDTO {
    private Long id;            // Asegúrate de que existe
    private String title;
    private String color;
    private TaskState taskState;
}