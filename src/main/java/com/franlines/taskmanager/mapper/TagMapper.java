package com.franlines.taskmanager.mapper;

import com.franlines.taskmanager.dto.tag.TagResponseDTO;
import com.franlines.taskmanager.persistence.model.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponseDTO toResponseDTO(Tag tag);

    List<TagResponseDTO> toResponseDTOList(List<Tag> tags);
}