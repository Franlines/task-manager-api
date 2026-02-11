package com.franlines.taskmanager.controller;

import com.franlines.taskmanager.dto.page.PageRequestDTO;
import com.franlines.taskmanager.dto.page.PageResponseDTO;
import com.franlines.taskmanager.dto.task.TaskCreateDTO;
import com.franlines.taskmanager.dto.task.TaskFilterDTO;
import com.franlines.taskmanager.dto.task.TaskResponseDTO;
import com.franlines.taskmanager.dto.task.TaskUpdateDTO;
import com.franlines.taskmanager.enums.TaskState;
import com.franlines.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // ========== CRUD BÁSICO ==========

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskCreateDTO taskCreateDTO,
            @RequestHeader("X-User-Id") Long userId) {

        log.info("Creando tarea para usuario ID: {}", userId);
        TaskResponseDTO createdTask = taskService.createTask(taskCreateDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable Long taskId,
            @RequestHeader("X-User-Id") Long userId) {

        log.debug("Obteniendo tarea ID: {} para usuario ID: {}", taskId, userId);
        TaskResponseDTO task = taskService.getTaskById(taskId, userId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateDTO taskUpdateDTO,
            @RequestHeader("X-User-Id") Long userId) {

        log.info("Actualizando tarea ID: {} para usuario ID: {}", taskId, userId);
        TaskResponseDTO updatedTask = taskService.updateTask(taskId, taskUpdateDTO, userId);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @RequestHeader("X-User-Id") Long userId) {

        log.info("Eliminando tarea ID: {} para usuario ID: {}", taskId, userId);
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    // ========== VISTAS ESPECÍFICAS ==========

    @GetMapping("/workspace/{workspaceId}/weekly")
    public ResponseEntity<PageResponseDTO<TaskResponseDTO>> getWeeklyView(
            @PathVariable Long workspaceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "all") String filterType,
            @Valid PageRequestDTO pageRequest) {

        log.debug("Vista semanal - Workspace: {}, Semana: {}, Usuario: {}",
                workspaceId, weekStart, userId);

        PageResponseDTO<TaskResponseDTO> tasks = taskService.getWeeklyViewPaginated(
                workspaceId, weekStart, userId, filterType,
                pageRequest.getPage(), pageRequest.getSize());

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/workspace/{workspaceId}/daily")
    public ResponseEntity<PageResponseDTO<TaskResponseDTO>> getDailyView(
            @PathVariable Long workspaceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "all") String filterType,
            @Valid PageRequestDTO pageRequest) {

        log.debug("Vista diaria - Workspace: {}, Día: {}, Usuario: {}",
                workspaceId, date, userId);

        PageResponseDTO<TaskResponseDTO> tasks = taskService.getDailyViewPaginated(
                workspaceId, date, userId, filterType,
                pageRequest.getPage(), pageRequest.getSize());

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/workspace/{workspaceId}/kanban")
    public ResponseEntity<Map<TaskState, List<TaskResponseDTO>>> getKanbanView(
            @PathVariable Long workspaceId,
            @RequestHeader("X-User-Id") Long userId) {

        log.debug("Vista Kanban - Workspace: {}, Usuario: {}", workspaceId, userId);
        Map<TaskState, List<TaskResponseDTO>> kanbanView = taskService.getKanbanView(workspaceId, userId);
        return ResponseEntity.ok(kanbanView);
    }

    // ========== FILTRADO Y BÚSQUEDA ==========

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<PageResponseDTO<TaskResponseDTO>> getTasksByWorkspace(
            @PathVariable Long workspaceId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid PageRequestDTO pageRequest) {

        PageResponseDTO<TaskResponseDTO> tasks = taskService.getTasksByWorkspacePaginated(
                workspaceId, userId,
                pageRequest.getPage(), pageRequest.getSize(),
                pageRequest.getSortBy(), pageRequest.getDirection());

        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/filter")
    public ResponseEntity<PageResponseDTO<TaskResponseDTO>> filterTasks(
            @Valid @RequestBody TaskFilterDTO filterDTO,
            @RequestHeader("X-User-Id") Long userId,
            @Valid PageRequestDTO pageRequest) {

        log.debug("Filtrando tareas para usuario ID: {}", userId);

        PageResponseDTO<TaskResponseDTO> tasks = taskService.filterTasksPaginated(
                filterDTO, userId,
                pageRequest.getPage(), pageRequest.getSize(),
                pageRequest.getSortBy(), pageRequest.getDirection());

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/workspace/{workspaceId}/search")
    public ResponseEntity<PageResponseDTO<TaskResponseDTO>> searchTasks(
            @PathVariable Long workspaceId,
            @RequestParam String query,
            @RequestHeader("X-User-Id") Long userId,
            @Valid PageRequestDTO pageRequest) {

        log.debug("Buscando tareas - Workspace: {}, Query: '{}'", workspaceId, query);

        PageResponseDTO<TaskResponseDTO> tasks = taskService.searchTasks(
                workspaceId, query, userId,
                pageRequest.getPage(), pageRequest.getSize());

        return ResponseEntity.ok(tasks);
    }

    // ========== OPERACIONES ESPECÍFICAS ==========

    @PatchMapping("/{taskId}/state")
    public ResponseEntity<TaskResponseDTO> changeTaskState(
            @PathVariable Long taskId,
            @RequestParam TaskState newState,
            @RequestHeader("X-User-Id") Long userId) {

        log.info("Cambiando estado de tarea ID: {} a {} para usuario ID: {}",
                taskId, newState, userId);

        TaskResponseDTO updatedTask = taskService.changeTaskState(taskId, newState, userId);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/workspace/{workspaceId}/user/{targetUserId}")
    public ResponseEntity<PageResponseDTO<TaskResponseDTO>> getTasksByUser(
            @PathVariable Long workspaceId,
            @PathVariable Long targetUserId,
            @RequestHeader("X-User-Id") Long currentUserId,
            @Valid PageRequestDTO pageRequest) {

        PageResponseDTO<TaskResponseDTO> tasks = taskService.getTasksByUserInWorkspace(
                targetUserId, workspaceId, currentUserId,
                pageRequest.getPage(), pageRequest.getSize(),
                pageRequest.getSortBy(), pageRequest.getDirection());

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/workspace/{workspaceId}/state/{state}")
    public ResponseEntity<PageResponseDTO<TaskResponseDTO>> getTasksByState(
            @PathVariable Long workspaceId,
            @PathVariable TaskState state,
            @RequestHeader("X-User-Id") Long userId,
            @Valid PageRequestDTO pageRequest) {

        PageResponseDTO<TaskResponseDTO> tasks = taskService.getTasksByState(
                workspaceId, state, userId,
                pageRequest.getPage(), pageRequest.getSize());

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/workspace/{workspaceId}/upcoming")
    public ResponseEntity<PageResponseDTO<TaskResponseDTO>> getUpcomingTasks(
            @PathVariable Long workspaceId,
            @RequestParam(defaultValue = "7") int daysAhead,
            @RequestHeader("X-User-Id") Long userId,
            @Valid PageRequestDTO pageRequest) {

        PageResponseDTO<TaskResponseDTO> tasks = taskService.getUpcomingTasks(
                workspaceId, daysAhead, userId,
                pageRequest.getPage(), pageRequest.getSize());

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/workspace/{workspaceId}/overdue")
    public ResponseEntity<PageResponseDTO<TaskResponseDTO>> getOverdueTasks(
            @PathVariable Long workspaceId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid PageRequestDTO pageRequest) {

        PageResponseDTO<TaskResponseDTO> tasks = taskService.getOverdueTasks(
                workspaceId, userId,
                pageRequest.getPage(), pageRequest.getSize());

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/workspace/{workspaceId}/statistics")
    public ResponseEntity<Map<String, Object>> getTaskStatistics(
            @PathVariable Long workspaceId,
            @RequestHeader("X-User-Id") Long userId) {

        Map<String, Object> statistics = taskService.getTaskStatistics(workspaceId, userId);
        return ResponseEntity.ok(statistics);
    }

    // ========== ENDPOINTS SIMPLIFICADOS (sin paginación) ==========

    @GetMapping("/workspace/{workspaceId}/all")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasksByWorkspace(
            @PathVariable Long workspaceId,
            @RequestHeader("X-User-Id") Long userId) {

        List<TaskResponseDTO> tasks = taskService.getTasksByWorkspace(workspaceId, userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/workspace/{workspaceId}/weekly/simple")
    public ResponseEntity<List<TaskResponseDTO>> getWeeklyViewSimple(
            @PathVariable Long workspaceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "all") String filterType) {

        List<TaskResponseDTO> tasks = taskService.getWeeklyView(workspaceId, weekStart, userId, filterType);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/workspace/{workspaceId}/daily/simple")
    public ResponseEntity<List<TaskResponseDTO>> getDailyViewSimple(
            @PathVariable Long workspaceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "all") String filterType) {

        List<TaskResponseDTO> tasks = taskService.getDailyView(workspaceId, date, userId, filterType);
        return ResponseEntity.ok(tasks);
    }

    // ========== ENDPOINT DE PRUEBA ==========

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        return ResponseEntity.ok(Map.of(
                "message", "Task API is working!",
                "status", "OK",
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}