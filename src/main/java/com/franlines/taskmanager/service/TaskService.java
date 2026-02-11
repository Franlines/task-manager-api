package com.franlines.taskmanager.service;

import com.franlines.taskmanager.dto.page.PageResponseDTO;
import com.franlines.taskmanager.dto.task.TaskCreateDTO;
import com.franlines.taskmanager.dto.task.TaskFilterDTO;
import com.franlines.taskmanager.dto.task.TaskResponseDTO;
import com.franlines.taskmanager.dto.task.TaskUpdateDTO;
import com.franlines.taskmanager.enums.TaskState;
import com.franlines.taskmanager.enums.WorkspaceRole;
import com.franlines.taskmanager.exception.BusinessException;
import com.franlines.taskmanager.exception.EntityNotFoundException;
import com.franlines.taskmanager.exception.PermissionDeniedException;
import com.franlines.taskmanager.persistence.model.*;
import com.franlines.taskmanager.persistence.repository.*;
import com.franlines.taskmanager.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final TagRepository tagRepository;
    private final com.franlines.taskmanager.mapper.TaskMapper taskMapper;

    private static final String TASK_NOT_FOUND = "Tarea no encontrada";
    private static final String WORKSPACE_NOT_FOUND = "Workspace no encontrado";
    private static final String USER_NOT_FOUND = "Usuario no encontrado";

    // ========== MÉTODOS CRUD ==========

    public TaskResponseDTO createTask(TaskCreateDTO dto, Long creatorUserId) {
        log.info("Creando nueva tarea. Creator: {}, Workspace: {}", creatorUserId, dto.getWorkspaceId());

        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        Workspace workspace = workspaceRepository.findById(dto.getWorkspaceId())
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        validateUserWorkspacePermission(creator, workspace, WorkspaceRole.EDITOR);

        User principalUser = userRepository.findById(dto.getPrincipalUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario principal no encontrado"));
        validateUserWorkspaceMembership(principalUser, workspace);

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPrincipalUser(principalUser);
        task.setWorkspace(workspace);
        task.setColor(dto.getColor());
        task.setDay(dto.getDay());
        task.setStartTime(dto.getStartTime());
        task.setEndTime(dto.getEndTime());
        task.setTaskState(dto.getTaskState() != null ? dto.getTaskState() : TaskState.TO_DO);

        // Usuarios secundarios
        if (dto.getSideUserIds() != null && !dto.getSideUserIds().isEmpty()) {
            List<User> sideUsers = userRepository.findAllById(dto.getSideUserIds());
            sideUsers.forEach(user -> validateUserWorkspaceMembership(user, workspace));
            task.setSideUsers(sideUsers);
        }

        // Tags
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(dto.getTagIds());
            validateTagsBelongToWorkspace(tags, workspace);
            task.setTags(tags);
        }

        Task savedTask = taskRepository.save(task);
        log.info("Tarea creada exitosamente. ID: {}", savedTask.getId());

        return taskMapper.toResponseDTO(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long taskId, Long userId) {
        log.debug("Obteniendo tarea ID: {} para usuario ID: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(TASK_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateUserWorkspaceAccess(user, task.getWorkspace());

        return taskMapper.toResponseDTO(task);
    }

    public TaskResponseDTO updateTask(Long taskId, TaskUpdateDTO dto, Long userId) {
        log.info("Actualizando tarea ID: {}. Usuario: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(TASK_NOT_FOUND));

        User editor = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateTaskEditPermission(task, editor);

        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getColor() != null) task.setColor(dto.getColor());
        if (dto.getTaskState() != null) task.setTaskState(dto.getTaskState());
        if (dto.getDay() != null) task.setDay(dto.getDay());
        if (dto.getStartTime() != null) task.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) task.setEndTime(dto.getEndTime());

        if (dto.getPrincipalUserId() != null) {
            User newPrincipal = userRepository.findById(dto.getPrincipalUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario principal no encontrado"));
            validateUserWorkspaceMembership(newPrincipal, task.getWorkspace());
            task.setPrincipalUser(newPrincipal);
        }

        if (dto.getSideUserIds() != null) {
            List<User> sideUsers = userRepository.findAllById(dto.getSideUserIds());
            sideUsers.forEach(user -> validateUserWorkspaceMembership(user, task.getWorkspace()));
            task.setSideUsers(sideUsers);
        }

        if (dto.getTagIds() != null) {
            List<Tag> tags = tagRepository.findAllById(dto.getTagIds());
            validateTagsBelongToWorkspace(tags, task.getWorkspace());
            task.setTags(tags);
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Tarea ID: {} actualizada exitosamente", taskId);

        return taskMapper.toResponseDTO(updatedTask);
    }

    public void deleteTask(Long taskId, Long userId) {
        log.info("Eliminando tarea ID: {}. Usuario: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(TASK_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateTaskDeletePermission(task, user);

        taskRepository.delete(task);
        log.info("Tarea ID: {} eliminada exitosamente", taskId);
    }

    // ========== VISTAS ESPECÍFICAS ==========

    @Transactional(readOnly = true)
    public PageResponseDTO<TaskResponseDTO> getWeeklyViewPaginated(
            Long workspaceId, LocalDate weekStart, Long userId,
            String filterType, int page, int size) {

        log.debug("Obteniendo vista semanal. Workspace: {}, Semana: {}, Usuario: {}",
                workspaceId, weekStart, userId);

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateUserWorkspaceAccess(user, workspace);

        LocalDate weekEnd = weekStart.plusDays(6);
        Pageable pageable = PaginationUtils.createPageable(page, size, "day", "asc");

        Page<Task> taskPage = taskRepository.findByWorkspaceAndDayBetween(
                workspace, weekStart, weekEnd, pageable);

        List<Task> filteredContent = applyUserFilter(taskPage.getContent(), userId, filterType);
        List<TaskResponseDTO> content = filteredContent.stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.of(taskPage, content);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<TaskResponseDTO> getDailyViewPaginated(
            Long workspaceId, LocalDate date, Long userId,
            String filterType, int page, int size) {

        log.debug("Obteniendo vista diaria. Workspace: {}, Día: {}, Usuario: {}",
                workspaceId, date, userId);

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateUserWorkspaceAccess(user, workspace);

        Pageable pageable = PaginationUtils.createPageable(page, size, "startTime", "asc");
        Page<Task> taskPage = taskRepository.findByWorkspaceAndDay(workspace, date, pageable);

        List<Task> filteredContent = applyUserFilter(taskPage.getContent(), userId, filterType);
        filteredContent.sort(Comparator
                .comparing(Task::getStartTime, Comparator.nullsFirst(Comparator.naturalOrder())));

        List<TaskResponseDTO> content = filteredContent.stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.of(taskPage, content);
    }

    @Transactional(readOnly = true)
    public Map<TaskState, List<TaskResponseDTO>> getKanbanView(Long workspaceId, Long userId) {
        log.debug("Obteniendo vista Kanban. Workspace: {}, Usuario: {}", workspaceId, userId);

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateUserWorkspaceAccess(user, workspace);

        List<Task> allTasks = taskRepository.findByWorkspace(workspace);
        Map<TaskState, List<Task>> groupedByState = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getTaskState));

        Map<TaskState, List<TaskResponseDTO>> result = new EnumMap<>(TaskState.class);

        for (TaskState state : TaskState.values()) {
            List<Task> tasksForState = groupedByState.getOrDefault(state, Collections.emptyList());
            tasksForState.sort(Comparator
                    .comparing(Task::getDay, Comparator.nullsFirst(Comparator.naturalOrder()))
                    .thenComparing(Task::getStartTime, Comparator.nullsFirst(Comparator.naturalOrder())));

            List<TaskResponseDTO> dtos = tasksForState.stream()
                    .map(taskMapper::toResponseDTO)
                    .collect(Collectors.toList());

            result.put(state, dtos);
        }

        return result;
    }

    // ========== FILTRADO Y BÚSQUEDA ==========
    @Transactional(readOnly = true)
    public PageResponseDTO<TaskResponseDTO> getTasksByWorkspacePaginated(
            Long workspaceId, Long userId, int page, int size, String sortBy, String direction) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateUserWorkspaceAccess(user, workspace);

        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, direction);
        Page<Task> taskPage = taskRepository.findByWorkspace(workspace, pageable);

        return PageResponseDTO.of(taskPage.map(taskMapper::toResponseDTO));
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<TaskResponseDTO> filterTasksPaginated(
            TaskFilterDTO filterDTO, Long userId,
            int page, int size, String sortBy, String direction) {

        log.debug("Filtrando tareas. Usuario: {}, Filtros: {}", userId, filterDTO);

        Workspace workspace = workspaceRepository.findById(filterDTO.getWorkspaceId())
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateUserWorkspaceAccess(user, workspace);

        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, direction);

        Page<Task> taskPage = taskRepository.findByFilters(
                workspace,
                filterDTO.getTaskState(),
                filterDTO.getStartDate(),
                filterDTO.getEndDate(),
                filterDTO.getUserId(),
                pageable
        );

        List<Task> filteredContent = taskPage.getContent();

        if (filterDTO.getTagIds() != null && !filterDTO.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(filterDTO.getTagIds());
            filteredContent = filteredContent.stream()
                    .filter(task -> task.getTags().stream()
                            .anyMatch(tag -> filterDTO.getTagIds().contains(tag.getId())))
                    .collect(Collectors.toList());
        }

        List<TaskResponseDTO> content = filteredContent.stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.of(taskPage, content);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<TaskResponseDTO> searchTasks(
            Long workspaceId, String query, Long userId, int page, int size) {

        log.debug("Buscando tareas. Workspace: {}, Query: '{}', Usuario: {}",
                workspaceId, query, userId);

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateUserWorkspaceAccess(user, workspace);

        Pageable pageable = PaginationUtils.createPageable(page, size, "title", "asc");
        Page<Task> taskPage = taskRepository.searchInWorkspace(workspace, query, pageable);

        return PageResponseDTO.of(taskPage.map(taskMapper::toResponseDTO));
    }

    // ========== MÉTODOS ESPECÍFICOS ==========

    public TaskResponseDTO changeTaskState(Long taskId, TaskState newState, Long userId) {
        log.info("Cambiando estado de tarea ID: {} a {}. Usuario: {}", taskId, newState, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(TASK_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateTaskEditPermission(task, user);
        validateStateTransition(task.getTaskState(), newState);

        task.setTaskState(newState);
        Task updatedTask = taskRepository.save(task);

        log.info("Estado de tarea ID: {} cambiado a {}", taskId, newState);

        return taskMapper.toResponseDTO(updatedTask);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<TaskResponseDTO> getTasksByUserInWorkspace(
            Long targetUserId, Long workspaceId, Long currentUserId,
            int page, int size, String sortBy, String direction) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario objetivo no encontrado"));

        validateUserWorkspaceAccess(currentUser, workspace);

        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, direction);
        Page<Task> taskPage = taskRepository.findByWorkspaceAndUser(workspace, targetUser, pageable);

        return PageResponseDTO.of(taskPage.map(taskMapper::toResponseDTO));
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<TaskResponseDTO> getTasksByState(
            Long workspaceId, TaskState state, Long userId, int page, int size) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateUserWorkspaceAccess(user, workspace);

        Pageable pageable = PaginationUtils.createPageable(page, size, "day", "asc");
        Page<Task> taskPage = taskRepository.findByWorkspaceAndTaskState(workspace, state, pageable);

        return PageResponseDTO.of(taskPage.map(taskMapper::toResponseDTO));
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<TaskResponseDTO> getUpcomingTasks(
            Long workspaceId, int daysAhead, Long userId, int page, int size) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateUserWorkspaceAccess(user, workspace);

        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);

        Pageable pageable = PaginationUtils.createPageable(page, size, "day", "asc");
        Page<Task> taskPage = taskRepository.findByWorkspaceAndDayBetween(
                workspace, today, futureDate, pageable);

        return PageResponseDTO.of(taskPage.map(taskMapper::toResponseDTO));
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<TaskResponseDTO> getOverdueTasks(
            Long workspaceId, Long userId, int page, int size) {

        log.debug("Obteniendo tareas vencidas. Workspace: {}, Usuario: {}", workspaceId, userId);

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateUserWorkspaceAccess(user, workspace);

        LocalDate today = LocalDate.now();
        Pageable pageable = PaginationUtils.createPageable(page, size, "day", "desc");
        Page<Task> taskPage = taskRepository.findByOverdueTasks(workspace, today, pageable);

        return PageResponseDTO.of(taskPage.map(taskMapper::toResponseDTO));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTaskStatistics(Long workspaceId, Long userId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateUserWorkspaceAccess(user, workspace);

        Map<String, Object> statistics = new HashMap<>();

        Map<TaskState, Long> countByState = taskRepository.countByWorkspaceAndTaskState(workspace);
        statistics.put("countByState", countByState);

        long totalTasks = countByState.values().stream().mapToLong(Long::longValue).sum();
        statistics.put("totalTasks", totalTasks);

        LocalDate today = LocalDate.now();
        List<Task> todayTasks = taskRepository.findByWorkspaceAndDay(workspace, today);
        statistics.put("todayTasks", todayTasks.size());

        LocalDate nextWeek = today.plusDays(7);
        List<Task> upcomingTasks = taskRepository.findByWorkspaceAndDayBetween(workspace, today, nextWeek);
        statistics.put("upcomingTasks", upcomingTasks.size());

        return statistics;
    }

    // ========== MÉTODOS AUXILIARES ==========

    private void validateUserWorkspacePermission(User user, Workspace workspace, WorkspaceRole requiredRole) {
        WorkspaceUser workspaceUser = workspaceUserRepository
                .findByUserAndWorkspace(user, workspace)
                .orElseThrow(() -> new PermissionDeniedException("Usuario no pertenece al workspace"));

        if (workspaceUser.getAccountState() != com.franlines.taskmanager.enums.AccountState.ACTIVE) {
            throw new PermissionDeniedException("Usuario no está activo en el workspace");
        }

        if (workspaceUser.getInvitationStatus() != com.franlines.taskmanager.enums.InvitationStatus.ACTIVE) {
            throw new PermissionDeniedException("Invitación pendiente de aceptación");
        }

        if (workspaceUser.getWorkspaceRole().ordinal() > requiredRole.ordinal()) {
            throw new PermissionDeniedException(
                    String.format("Se requiere rol %s o superior", requiredRole)
            );
        }
    }

    private void validateUserWorkspaceMembership(User user, Workspace workspace) {
        boolean isMember = workspaceUserRepository
                .findByUserAndWorkspace(user, workspace)
                .map(wu -> wu.getAccountState() == com.franlines.taskmanager.enums.AccountState.ACTIVE &&
                        wu.getInvitationStatus() == com.franlines.taskmanager.enums.InvitationStatus.ACTIVE)
                .orElse(false);

        if (!isMember) {
            throw new PermissionDeniedException("Usuario no es miembro activo del workspace");
        }
    }

    private void validateUserWorkspaceAccess(User user, Workspace workspace) {
        validateUserWorkspaceMembership(user, workspace);
    }

    private void validateTaskEditPermission(Task task, User user) {
        if (task.getPrincipalUser().getId().equals(user.getId())) {
            return;
        }

        boolean isSecondary = task.getSideUsers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));

        if (isSecondary) {
            return;
        }

        validateUserWorkspacePermission(user, task.getWorkspace(), WorkspaceRole.EDITOR);
    }

    private void validateTaskDeletePermission(Task task, User user) {
        boolean isPrincipal = task.getPrincipalUser().getId().equals(user.getId());

        if (!isPrincipal) {
            validateUserWorkspacePermission(user, task.getWorkspace(), WorkspaceRole.ADMIN);
        }
    }

    private void validateTagsBelongToWorkspace(List<Tag> tags, Workspace workspace) {
        tags.forEach(tag -> {
            if (!tag.getWorkspace().getId().equals(workspace.getId())) {
                throw new BusinessException(
                        String.format("El tag '%s' no pertenece al workspace", tag.getName())
                );
            }
        });
    }

    private void validateStateTransition(TaskState currentState, TaskState newState) {
        // Implementar reglas de negocio si es necesario
    }

    private List<Task> applyUserFilter(List<Task> tasks, Long userId, String filterType) {
        if (filterType == null || filterType.equalsIgnoreCase("all")) {
            return tasks;
        }

        switch (filterType.toLowerCase()) {
            case "user":
                return tasks.stream()
                        .filter(task -> task.getPrincipalUser().getId().equals(userId) ||
                                task.getSideUsers().stream().anyMatch(u -> u.getId().equals(userId)))
                        .collect(Collectors.toList());
            case "workspace":
                return tasks;
            default:
                return tasks;
        }
    }

    // ========== MÉTODOS SIMPLIFICADOS (sin paginación) ==========

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getWeeklyView(Long workspaceId, LocalDate weekStart, Long userId, String filterType) {
        PageResponseDTO<TaskResponseDTO> pageResult = getWeeklyViewPaginated(
                workspaceId, weekStart, userId, filterType, 0, Integer.MAX_VALUE);
        return pageResult.getContent();
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getDailyView(Long workspaceId, LocalDate date, Long userId, String filterType) {
        PageResponseDTO<TaskResponseDTO> pageResult = getDailyViewPaginated(
                workspaceId, date, userId, filterType, 0, Integer.MAX_VALUE);
        return pageResult.getContent();
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByWorkspace(Long workspaceId, Long userId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        validateUserWorkspaceAccess(user, workspace);

        List<Task> tasks = taskRepository.findByWorkspace(workspace);
        return tasks.stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}