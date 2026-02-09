package com.franlines.taskmanager.mapper;

import com.franlines.taskmanager.dto.user.UserResponseDTO;
import com.franlines.taskmanager.persistence.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponseDTO(User user);

    List<UserResponseDTO> toResponseDTOList(List<User> users);
}