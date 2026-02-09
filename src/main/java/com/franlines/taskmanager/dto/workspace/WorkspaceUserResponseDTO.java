package com.franlines.taskmanager.dto.workspace;

import com.franlines.taskmanager.dto.user.UserResponseDTO;
import com.franlines.taskmanager.enums.AccountState;
import com.franlines.taskmanager.enums.InvitationStatus;
import com.franlines.taskmanager.enums.WorkspaceRole;

public class WorkspaceUserResponseDTO {
    private UserResponseDTO user;
    private WorkspaceRole role;
    private InvitationStatus invitationStatus;
    private AccountState accountState;
}