package com.franlines.taskmanager.persistence.repository;

import com.franlines.taskmanager.persistence.model.User;
import com.franlines.taskmanager.persistence.model.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    boolean existsByName(String name);

    // Búsquedas paginadas
    Page<Workspace> findAll(Pageable pageable);
    Page<Workspace> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Workspace> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);  // Actualizado

    // Búsqueda combinada
    @Query("SELECT w FROM Workspace w WHERE " +
            "LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(w.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Workspace> searchWorkspaces(@Param("search") String search, Pageable pageable);

    // Workspaces por creador
    Page<Workspace> findByCreatedBy(User createdBy, Pageable pageable);

    // Workspaces donde el usuario es miembro
    @Query("SELECT w FROM Workspace w JOIN WorkspaceUser wu ON w.id = wu.workspace.id " +
            "WHERE wu.user = :user")
    Page<Workspace> findByMemberUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT w FROM Workspace w JOIN WorkspaceUser wu ON w.id = wu.workspace.id " +
            "WHERE wu.user = :user AND wu.accountState = 'ACTIVE' " +
            "AND wu.invitationStatus = 'ACTIVE'")
    Page<Workspace> findByActiveMemberUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT w FROM Workspace w JOIN WorkspaceUser wu ON w.id = wu.workspace.id " +
            "WHERE wu.user = :user AND wu.invitationStatus = 'INVITED'")
    Page<Workspace> findByInvitedMemberUser(@Param("user") User user, Pageable pageable);

    // Workspaces por rol de usuario
    @Query("SELECT w FROM Workspace w JOIN WorkspaceUser wu ON w.id = wu.workspace.id " +
            "WHERE wu.user = :user AND wu.workspaceRole = :role")
    Page<Workspace> findByMemberUserAndRole(
            @Param("user") User user,
            @Param("role") String role,
            Pageable pageable
    );

    // Workspaces creados por usuario
    List<Workspace> findByCreatedBy(User createdBy);
}