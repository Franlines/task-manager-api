package com.franlines.taskmanager.mapper;

import com.franlines.taskmanager.dto.task.TaskCreateDTO;
import com.franlines.taskmanager.dto.task.TaskResponseDTO;
import com.franlines.taskmanager.persistence.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = { UserMapper.class, TagMapper.class }
)
public interface TaskMapper {

    // Entity → Response
    @Mapping(target = "principalUser", source = "principalUser")
    @Mapping(target = "sideUsers", source = "sideUsers")
    @Mapping(target = "tags", source = "tags")
    TaskResponseDTO toResponseDTO(Task task);

    List<TaskResponseDTO> toResponseDTOList(List<Task> tasks);

    // CreateDTO → Entity (para algunos servicios)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "principalUser", ignore = true)
    @Mapping(target = "workspace", ignore = true)
    @Mapping(target = "sideUsers", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    Task toEntity(TaskCreateDTO dto);
}