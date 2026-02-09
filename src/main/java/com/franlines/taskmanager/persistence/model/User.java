package com.franlines.taskmanager.persistence.model;

import com.franlines.taskmanager.enums.UserRol;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
    Entidad que representa a un usuario del sistema
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    /**
     * Identificador único del usuario
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    /**
     * Nombre de usuario único utilizado para autenticación.
     */
    @NotBlank (message = "El nombre de usuario no debe estar vacío")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /**
    Contraseña del usuario. (Se almacena encriptada)
     */
    @NotBlank (message = "La contraseña no debe estar vacía")
    @Size(min = 8, message = "La contraseña debe tener más de 8 caracteres")
    private String password;

    /**
     * Rol del usuario dentro del sistema
     * ADMIN, EDITOR, USER
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRol userRol;

    /**
     * Nombre y apellidos reales del usuario para posible identificación
     */
    @NotBlank (message = "El nombre del usuario no debe estar vacío")
    @Size(min = 3, max = 40, message = "El nombre del usuario debe tener entre 3 y 40 caracteres")
    @Column(name = "firstname", nullable = false)
    private String firstName;

    @NotBlank (message = "Los apellidos de usuario no debe estar vacío")
    @Size(min = 3, max = 40, message = "El nombre de usuario debe tener entre 3 y 40 caracteres")
    @Column(name = "lastname", nullable = false)
    private String lastname;


    /**
     * Correo del usuario para posibles confirmaciones de registros. Debe ser único.
     */
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    @Size(max = 90, message = "El email no puede tener más de 90 caracteres")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    /**
     * Fecha de registro del usuario.
     * Se asigna automáticamente al crear la entidad.
     */
    @Column(name = "registered_date", nullable = false, updatable = false)
    private LocalDateTime registeredDate;

    /**
     * Fecha de nacimiento del usuario. (Opcional)
     */
    @Column(name = "birth_date")
    LocalDate birthDate;

    /**
     * URL o Path de la foto del usuario. (Opcional)
     */
    @Column(name = "photo_url")
    private String photoUrl;

    /**
     * Callback JPA que inicializa la fecha de registro.
     */
    @PrePersist
    protected void onCreate() {
        this.registeredDate = LocalDateTime.now();
    }
}
