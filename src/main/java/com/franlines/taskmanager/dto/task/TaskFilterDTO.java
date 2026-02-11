package com.franlines.taskmanager.dto.task;

import com.franlines.taskmanager.enums.TaskState;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class TaskFilterDTO {
    private Long workspaceId;
    private String searchText;
    private TaskState taskState;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long userId;
    private List<Long> tagIds;
    private String color;
    private LocalTime minStartTime;
    private LocalTime maxStartTime;
    private String sortBy;
    private String sortDirection = "asc";
    private String viewType;
}