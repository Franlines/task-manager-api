package com.franlines.taskmanager.mapper;

import com.franlines.taskmanager.dto.user.UserCreateDTO;
import com.franlines.taskmanager.dto.user.UserResponseDTO;
import com.franlines.taskmanager.persistence.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity to Response DTO
    UserResponseDTO toResponseDTO(User user);

    // Create DTO to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userRol", ignore = true)
    @Mapping(target = "registeredDate", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "photoUrl", ignore = true)
    @Mapping(target = "lastname", source = "lastName") // Si tu DTO usa lastName pero entidad usa lastname
    User toEntity(UserCreateDTO dto);

    // List mappings
    List<UserResponseDTO> toResponseDTOList(List<User> users);
}