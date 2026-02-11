package com.franlines.taskmanager.dto.user;

import lombok.Data;

@Data
public class UserCreateDTO {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
}