package com.franlines.taskmanager.mapper;

import com.franlines.taskmanager.dto.tag.TagCreateDTO;
import com.franlines.taskmanager.dto.tag.TagResponseDTO;
import com.franlines.taskmanager.persistence.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {

    // Entity to Response DTO
    TagResponseDTO toResponseDTO(Tag tag);

    // Create DTO to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workspace", ignore = true)
    Tag toEntity(TagCreateDTO dto);

    // List mappings
    List<TagResponseDTO> toResponseDTOList(List<Tag> tags);
}