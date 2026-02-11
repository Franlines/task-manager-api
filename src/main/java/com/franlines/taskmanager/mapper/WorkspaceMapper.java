package com.franlines.taskmanager.mapper;

import com.franlines.taskmanager.dto.workspace.WorkspaceResponseDTO;
import com.franlines.taskmanager.dto.workspace.WorkspaceCreateDTO;
import com.franlines.taskmanager.dto.workspace.WorkspaceUpdateDTO;
import com.franlines.taskmanager.persistence.model.Workspace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {

    // Entity to Response DTO
    @Mapping(target = "createdBy", ignore = true) // Esto se manejará en el servicio
    WorkspaceResponseDTO toResponseDTO(Workspace workspace);

    // Create DTO to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    Workspace toEntity(WorkspaceCreateDTO dto);

    // Update DTO to Entity (para actualizaciones)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void updateEntityFromDTO(WorkspaceUpdateDTO dto, @MappingTarget Workspace workspace);

    // List mappings
    List<WorkspaceResponseDTO> toResponseDTOList(List<Workspace> workspaces);
}