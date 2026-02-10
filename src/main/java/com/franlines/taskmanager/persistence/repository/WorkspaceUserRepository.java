package com.franlines.taskmanager.persistence.repository;

import com.franlines.taskmanager.enums.AccountState;
import com.franlines.taskmanager.enums.InvitationStatus;
import com.franlines.taskmanager.enums.WorkspaceRole;
import com.franlines.taskmanager.persistence.model.User;
import com.franlines.taskmanager.persistence.model.Workspace;
import com.franlines.taskmanager.persistence.model.WorkspaceUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceUserRepository extends JpaRepository<WorkspaceUser, Long> {

    // Métodos existentes
    Optional<WorkspaceUser> findByUserAndWorkspace(User user, Workspace workspace);
    List<WorkspaceUser> findByWorkspace(Workspace workspace);
    List<WorkspaceUser> findByUser(User user);

    List<WorkspaceUser> findByWorkspaceAndInvitationStatusAndAccountState(
            Workspace workspace,
            InvitationStatus invitationStatus,
            AccountState accountState
    );

    // Métodos paginados
    Page<WorkspaceUser> findAll(Pageable pageable);

    // Métodos existentes con paginación
    Page<WorkspaceUser> findByWorkspace(Workspace workspace, Pageable pageable);
    Page<WorkspaceUser> findByUser(User user, Pageable pageable);

    // Búsquedas paginadas con filtros
    Page<WorkspaceUser> findByWorkspaceAndWorkspaceRole(
            Workspace workspace,
            WorkspaceRole role,
            Pageable pageable
    );

    Page<WorkspaceUser> findByWorkspaceAndAccountState(
            Workspace workspace,
            AccountState accountState,
            Pageable pageable
    );

    Page<WorkspaceUser> findByWorkspaceAndInvitationStatus(
            Workspace workspace,
            InvitationStatus invitationStatus,
            Pageable pageable
    );

    Page<WorkspaceUser> findByWorkspaceAndInvitationStatusAndAccountState(
            Workspace workspace,
            InvitationStatus invitationStatus,
            AccountState accountState,
            Pageable pageable
    );

    // Métodos booleanos
    boolean existsByUserAndWorkspace(User user, Workspace workspace);
    boolean existsByUserAndWorkspaceAndAccountState(User user, Workspace workspace, AccountState accountState);
    boolean existsByUserAndWorkspaceAndInvitationStatus(User user, Workspace workspace, InvitationStatus status);

    // Contadores (sin paginación)
    long countByWorkspace(Workspace workspace);
    long countByWorkspaceAndWorkspaceRole(Workspace workspace, WorkspaceRole role);
    long countByWorkspaceAndAccountState(Workspace workspace, AccountState accountState);
    long countByWorkspaceAndInvitationStatus(Workspace workspace, InvitationStatus invitationStatus);

    // Búsqueda de miembros activos con información de usuario
    @Query("SELECT wu FROM WorkspaceUser wu JOIN FETCH wu.user u WHERE wu.workspace = :workspace AND wu.accountState = 'ACTIVE' " +
            "AND wu.invitationStatus = 'ACTIVE' " +
            "AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastname) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<WorkspaceUser> searchActiveMembers(
            @Param("workspace") Workspace workspace,
            @Param("search") String search,
            Pageable pageable
    );

    // Para dashboard - miembros por rol
    @Query("SELECT wu.workspaceRole, COUNT(wu) FROM WorkspaceUser wu " +
            "WHERE wu.workspace = :workspace AND wu.accountState = 'ACTIVE' " +
            "GROUP BY wu.workspaceRole")
    List<Object[]> countMembersByRole(@Param("workspace") Workspace workspace);
}