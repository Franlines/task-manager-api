package com.franlines.taskmanager.persistence.repository;

import com.franlines.taskmanager.persistence.model.Tag;
import com.franlines.taskmanager.persistence.model.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByName(String name);
    List<Tag> findByWorkspace(Workspace workspace);
    List<Tag> findByIdIn(List<Long> ids);
    Optional<Tag> findByNameAndWorkspace(String name, Workspace workspace);

    // Métodos paginados
    Page<Tag> findAll(Pageable pageable);

    // Métodos por workspace
    Page<Tag> findByWorkspace(Workspace workspace, Pageable pageable);

    // Búsquedas paginadas
    Page<Tag> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Tag> findByWorkspaceAndNameContainingIgnoreCase(
            Workspace workspace,
            String name,
            Pageable pageable
    );

    // Búsqueda combinada por workspace
    @Query("SELECT t FROM Tag t WHERE t.workspace = :workspace AND " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Tag> searchByWorkspace(
            @Param("workspace") Workspace workspace,
            @Param("search") String search,
            Pageable pageable
    );

    // Validaciones específicas por workspace
    boolean existsByNameAndWorkspace(String name, Workspace workspace);

    // Tags más usados en un workspace
    @Query("SELECT t, COUNT(t) as usageCount FROM Task task JOIN task.tags t " +
            "WHERE task.workspace = :workspace " +
            "GROUP BY t.id ORDER BY usageCount DESC")
    Page<Object[]> findMostUsedTags(@Param("workspace") Workspace workspace, Pageable pageable);
}