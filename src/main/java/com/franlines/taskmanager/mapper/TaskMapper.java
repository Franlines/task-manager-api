package com.franlines.taskmanager.mapper;

import com.franlines.taskmanager.dto.task.TaskResponseDTO;
import com.franlines.taskmanager.dto.task.TaskCreateDTO;
import com.franlines.taskmanager.dto.task.TaskSummaryDTO;
import com.franlines.taskmanager.dto.task.TaskUpdateDTO;
import com.franlines.taskmanager.persistence.model.Task;
import com.franlines.taskmanager.persistence.model.User;
import com.franlines.taskmanager.persistence.model.Workspace;
import com.franlines.taskmanager.persistence.model.Tag;
import com.franlines.taskmanager.enums.TaskState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, WorkspaceMapper.class, TagMapper.class}
)
public interface TaskMapper {

    // Entity to Response DTO
    @Mapping(target = "workspace", source = "workspace")
    @Mapping(target = "principalUser", source = "principalUser")
    @Mapping(target = "sideUsers", source = "sideUsers")
    @Mapping(target = "tags", source = "tags")
    TaskResponseDTO toResponseDTO(Task task);

    // Entity to Summary DTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "color", source = "color")
    @Mapping(target = "taskState", source = "taskState")
    TaskSummaryDTO toSummaryDTO(Task task);

    // Create DTO to Entity (este método se implementará manualmente)
    default Task toEntity(TaskCreateDTO dto, Workspace workspace, User principalUser,
                          List<User> sideUsers, List<Tag> tags) {
        if (dto == null) {
            return null;
        }

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setWorkspace(workspace);
        task.setPrincipalUser(principalUser);
        task.setSideUsers(sideUsers);
        task.setColor(dto.getColor());
        task.setDay(dto.getDay());
        task.setStartTime(dto.getStartTime());
        task.setEndTime(dto.getEndTime());
        task.setTags(tags);
        task.setTaskState(dto.getTaskState() != null ? dto.getTaskState() : TaskState.TO_DO);

        return task;
    }

    // Update DTO to Entity (se implementará en el servicio)
    default void updateEntityFromDTO(TaskUpdateDTO dto, Task task,
                                     User principalUser, List<User> sideUsers, List<Tag> tags) {
        if (dto == null || task == null) {
            return;
        }

        if (dto.getTitle() != null) {
            task.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }
        if (principalUser != null) {
            task.setPrincipalUser(principalUser);
        }
        if (sideUsers != null) {
            task.setSideUsers(sideUsers);
        }
        if (dto.getColor() != null) {
            task.setColor(dto.getColor());
        }
        if (dto.getDay() != null) {
            task.setDay(dto.getDay());
        }
        if (dto.getStartTime() != null) {
            task.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            task.setEndTime(dto.getEndTime());
        }
        if (tags != null) {
            task.setTags(tags);
        }
        if (dto.getTaskState() != null) {
            task.setTaskState(dto.getTaskState());
        }
    }

    // List mappings
    List<TaskResponseDTO> toResponseDTOList(List<Task> tasks);
    List<TaskSummaryDTO> toSummaryDTOList(List<Task> tasks);
}