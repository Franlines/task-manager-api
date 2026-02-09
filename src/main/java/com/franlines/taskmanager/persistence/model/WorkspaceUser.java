package com.franlines.taskmanager.persistence.model;

import com.franlines.taskmanager.enums.AccountState;
import com.franlines.taskmanager.enums.InvitationStatus;
import com.franlines.taskmanager.enums.WorkspaceRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad de asociación que representa la relación entre un usuario y un workspace.
 * Contiene información adicional como el estado de la invitación y el estado de la cuenta.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "workspace_users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "workspace_id"})
        }
)
public class WorkspaceUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_user_id", nullable = false)
    Long id;

    /**
     * Usuario asociado al espacio de trabajo.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    User user;

    /**
     * Espacio de trabajo al que pertenece el usuario.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    /**
     * Estado de la invitación del usuario al workspace
     * (INVITED, ACTIVE)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_invitation_status", nullable = false)
    InvitationStatus invitationStatus;

    /**-
     * Estado de la cuenta del usuario dentro del workspace
     * (ACTIVE, BANNED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    AccountState accountState;

    /**
     * Fecha en la que el usuario queda registrado en el workspace.
     * Se asigna automáticamente al persistir la entidad.
     */
    @Column(name = "registered_date", nullable = false, updatable = false)
    private LocalDateTime registeredDate;


    @Enumerated(EnumType.STRING)
    @Column(name = "workspace_role")
    private WorkspaceRole workspaceRole;  // Necesitas crear este enum

    /**
     * Inicializa los valores por defecto antes de persistir la entidad.
     */
    @PrePersist
    protected void onCreate() {
        this.registeredDate = LocalDateTime.now();


        if (this.invitationStatus == null) {
            this.invitationStatus = InvitationStatus.INVITED;
        }

        if (this.accountState == null) {
            this.accountState = AccountState.ACTIVE;
        }
    }
}
