package com.franlines.taskmanager.persistance.model;

import com.franlines.taskmanager.enums.UserState;
import com.franlines.taskmanager.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "workspace_users")
public class WorkspaceUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_user_id", nullable = false)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_invitation_status", nullable = false)
    UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    UserState userState;

    @Column(name = "registered_date", nullable = false, updatable = false)
    private LocalDateTime registeredDate;

    @PrePersist
    protected void onCreate() {
        this.registeredDate = LocalDateTime.now();
    }
}
