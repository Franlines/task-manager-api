package com.franlines.taskmanager.dto.task;

import com.franlines.taskmanager.enums.TaskState;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TaskUpdateDTO {
    private String title;
    private String description;
    private Long principalUserId;
    private List<Long> sideUserIds;
    private String color;
    private List<Long> tagIds;
    private TaskState taskState;
    private LocalDate day;
    private LocalTime startTime;
    private LocalTime endTime;
}