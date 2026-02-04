package com.franlines.taskmanager.persistance.model;

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

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @NotBlank (message = "El nombre de usuario no debe estar vacío")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank (message = "La contraseña no debe estar vacía")
    @Size(min = 8, message = "La contraseña debe tener más de 8 caracteres")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRol userRol;

    @NotBlank (message = "El nombre del usuario no debe estar vacío")
    @Size(min = 3, max = 40, message = "El nombre del usuario debe tener entre 3 y 40 caracteres")
    @Column(name = "firstname", nullable = false)
    private String firstName;

    @NotBlank (message = "Los apellidos de usuario no debe estar vacío")
    @Size(min = 3, max = 40, message = "El nombre de usuario debe tener entre 3 y 40 caracteres")
    @Column(name = "lastname", nullable = false)
    private String lastname;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    @Size(max = 90, message = "El email no puede tener más de 90 caracteres")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "registered_date", nullable = false, updatable = false)
    private LocalDateTime registeredDate;

    @PrePersist
    protected void onCreate() {
        this.registeredDate = LocalDateTime.now();
    }

    @Column(name = "birth_date")
    LocalDate birthDate;
}
