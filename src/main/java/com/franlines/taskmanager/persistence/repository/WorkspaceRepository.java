package com.franlines.taskmanager.persistence.repository;

import com.franlines.taskmanager.persistence.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    boolean existsByName(String name);
}
