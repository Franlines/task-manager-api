package com.franlines.taskmanager.mapper;

import com.franlines.taskmanager.dto.workspace.WorkspaceResponseDTO;
import com.franlines.taskmanager.persistence.model.Workspace;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {
    WorkspaceResponseDTO toResponseDTO(Workspace workspace);
}