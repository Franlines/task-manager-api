package com.franlines.taskmanager.persistence.repository;

import com.franlines.taskmanager.persistence.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Métodos existentes (sin paginación)
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Métodos existentes con paginación
    Page<User> findAll(Pageable pageable);

    // Búsquedas paginadas - Usando 'lastname' (minúscula como en la entidad)
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<User> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
    Page<User> findByLastnameContainingIgnoreCase(String lastname, Pageable pageable);  // lastname en minúscula

    // Búsqueda combinada
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastname) LIKE LOWER(CONCAT('%', :search, '%'))")  // lastname en minúscula
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    // Para invitaciones - usuarios por emails
    @Query("SELECT u FROM User u WHERE u.email IN :emails")
    List<User> findByEmails(@Param("emails") List<String> emails);

    // Usuarios por IDs (mantenemos List para operaciones batch)
    List<User> findByIdIn(List<Long> ids);

    // Búsqueda por nombre completo
    @Query("SELECT u FROM User u WHERE " +
            "CONCAT(LOWER(u.firstName), ' ', LOWER(u.lastname)) LIKE LOWER(CONCAT('%', :fullName, '%')) OR " +
            "CONCAT(LOWER(u.lastname), ' ', LOWER(u.firstName)) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    Page<User> findByFullNameContainingIgnoreCase(@Param("fullName") String fullName, Pageable pageable);
}