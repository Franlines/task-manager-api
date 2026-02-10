package com.franlines.taskmanager.persistence.repository;

import com.franlines.taskmanager.enums.TaskState;
import com.franlines.taskmanager.persistence.model.Tag;
import com.franlines.taskmanager.persistence.model.Task;
import com.franlines.taskmanager.persistence.model.User;
import com.franlines.taskmanager.persistence.model.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // ========== MÉTODOS SIN PAGINACIÓN ==========
    List<Task> findByWorkspace(Workspace workspace);
    List<Task> findByPrincipalUser(User user);
    List<Task> findByTaskState(TaskState state);
    List<Task> findByWorkspaceAndTaskState(Workspace workspace, TaskState state);
    List<Task> findByCreationDateBetween(LocalDateTime start, LocalDateTime end);
    List<Task> findByWorkspaceAndDayBetween(Workspace workspace, LocalDate start, LocalDate end);
    List<Task> findByWorkspaceAndDay(Workspace workspace, LocalDate day);

    // ========== MÉTODOS CON PAGINACIÓN ==========
    Page<Task> findAll(Pageable pageable);
    Page<Task> findByWorkspace(Workspace workspace, Pageable pageable);
    Page<Task> findByPrincipalUser(User user, Pageable pageable);
    Page<Task> findByTaskState(TaskState state, Pageable pageable);
    Page<Task> findByWorkspaceAndTaskState(Workspace workspace, TaskState state, Pageable pageable);

    // Métodos para vistas específicas con paginación
    Page<Task> findByWorkspaceAndDayBetween(Workspace workspace, LocalDate start, LocalDate end, Pageable pageable);
    Page<Task> findByWorkspaceAndDay(Workspace workspace, LocalDate day, Pageable pageable);

    // Métodos por usuario en workspace
    @Query("SELECT t FROM Task t WHERE t.workspace = :workspace AND t.principalUser = :user")
    Page<Task> findByWorkspaceAndPrincipalUser(
            @Param("workspace") Workspace workspace,
            @Param("user") User user,
            Pageable pageable
    );

    @Query("SELECT t FROM Task t JOIN t.sideUsers su WHERE t.workspace = :workspace AND su = :user")
    Page<Task> findByWorkspaceAndSideUser(
            @Param("workspace") Workspace workspace,
            @Param("user") User user,
            Pageable pageable
    );

    // Tareas donde el usuario es principal o secundario
    @Query("SELECT t FROM Task t WHERE t.workspace = :workspace AND " +
            "(t.principalUser = :user OR :user MEMBER OF t.sideUsers)")
    Page<Task> findByWorkspaceAndUser(
            @Param("workspace") Workspace workspace,
            @Param("user") User user,
            Pageable pageable
    );

    // Método genérico para filtros
    @Query("SELECT t FROM Task t WHERE " +
            "(:workspace IS NULL OR t.workspace = :workspace) AND " +
            "(:state IS NULL OR t.taskState = :state) AND " +
            "(:startDate IS NULL OR t.day >= :startDate) AND " +
            "(:endDate IS NULL OR t.day <= :endDate) AND " +
            "(:userId IS NULL OR t.principalUser.id = :userId OR " +
            "EXISTS (SELECT su FROM t.sideUsers su WHERE su.id = :userId))")
    Page<Task> findByFilters(
            @Param("workspace") Workspace workspace,
            @Param("state") TaskState state,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userId") Long userId,
            Pageable pageable
    );

    // Método para filtros con tags
    @Query("SELECT DISTINCT t FROM Task t JOIN t.tags tag WHERE " +
            "t.workspace = :workspace AND tag IN :tags")
    Page<Task> findByWorkspaceAndTagsIn(
            @Param("workspace") Workspace workspace,
            @Param("tags") List<Tag> tags,
            Pageable pageable
    );

    // Búsqueda por texto (título o descripción)
    @Query("SELECT t FROM Task t WHERE t.workspace = :workspace AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Task> searchInWorkspace(
            @Param("workspace") Workspace workspace,
            @Param("search") String search,
            Pageable pageable
    );

    // Métodos para estadísticas
    @Query("SELECT t.taskState, COUNT(t) FROM Task t WHERE t.workspace = :workspace GROUP BY t.taskState")
    Map<TaskState, Long> countByWorkspaceAndTaskState(@Param("workspace") Workspace workspace);

    @Query("SELECT t.day, COUNT(t) FROM Task t WHERE t.workspace = :workspace " +
            "AND t.day BETWEEN :start AND :end GROUP BY t.day ORDER BY t.day")
    List<Object[]> countTasksByDay(
            @Param("workspace") Workspace workspace,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // Tareas próximas (hoy y futuras)
    @Query("SELECT t FROM Task t WHERE t.workspace = :workspace AND t.day >= :today " +
            "ORDER BY t.day ASC, t.startTime ASC")
    Page<Task> findUpcomingTasks(
            @Param("workspace") Workspace workspace,
            @Param("today") LocalDate today,
            Pageable pageable
    );

    // Tareas vencidas (con día anterior a hoy y estado no completado)
    @Query("SELECT t FROM Task t WHERE t.workspace = :workspace AND t.day < :today " +
            "AND t.taskState != 'DONE' ORDER BY t.day DESC")
    Page<Task> findOverdueTasks(
            @Param("workspace") Workspace workspace,
            @Param("today") LocalDate today,
            Pageable pageable
    );
}