package com.franlines.taskmanager.persistence.repository;

import com.franlines.taskmanager.persistence.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {


    boolean existsByName(String name);
}
