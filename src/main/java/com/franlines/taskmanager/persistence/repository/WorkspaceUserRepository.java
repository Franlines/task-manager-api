package com.franlines.taskmanager.persistence.repository;

import com.franlines.taskmanager.enums.AccountState;
import com.franlines.taskmanager.persistence.model.User;
import com.franlines.taskmanager.persistence.model.Workspace;
import com.franlines.taskmanager.persistence.model.WorkspaceUser;
import com.franlines.taskmanager.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceUserRepository extends JpaRepository<WorkspaceUser, Long> {
    Optional<WorkspaceUser> findByUserAndWorkspace(User user, Workspace workspace);

    List<WorkspaceUser> findByWorkspace(Workspace workspace);

    List<WorkspaceUser> findByUser(User user);

    List<WorkspaceUser> findByWorkspaceAndInvitationStatusAndAccountState(
            Workspace workspace,
            InvitationStatus invitationStatus,
            AccountState accountState
    );
}
