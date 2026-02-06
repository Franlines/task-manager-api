package com.franlines.taskmanager.persistence.repository;

import com.franlines.taskmanager.persistence.model.Task;
import com.franlines.taskmanager.persistence.model.User;
import com.franlines.taskmanager.persistence.model.Workspace;
import com.franlines.taskmanager.enums.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByWorkspace(Workspace workspace);

    List<Task> findByPrincipalUser(User user);

    List<Task> findByTaskState(TaskState state);

    List<Task> findByWorkspaceAndTaskState(Workspace workspace, TaskState state);

    List<Task> findByCreationDateBetween(LocalDateTime start, LocalDateTime end);
}
