package com.franlines.taskmanager.dto.task;

import com.franlines.taskmanager.dto.tag.TagResponseDTO;
import com.franlines.taskmanager.dto.user.UserResponseDTO;
import com.franlines.taskmanager.enums.TaskState;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private UserResponseDTO principalUser;
    private List<UserResponseDTO> sideUsers;
    private String color;
    private TaskState taskState;
    private List<TagResponseDTO> tags;
    private LocalDateTime creationDate;
    private LocalDateTime updatedDate;
    private LocalDate day;
    private LocalTime startTime;
    private LocalTime endTime;
}