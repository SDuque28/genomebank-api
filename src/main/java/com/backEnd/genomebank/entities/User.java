package com.backEnd.genomebank.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToMany(fetch = FetchType.EAGER) // Relación muchos a muchos con roles, carga inmediata
    @JoinTable(
            name = "usuario_rol", // Tabla intermedia
            joinColumns = @JoinColumn(name = "user_id"), // FK usuario
            inverseJoinColumns = @JoinColumn(name = "rol_id") // FK rol
    )
    private Set<Rol> roles = new HashSet<>(); // Conjunto de roles asignados al usuario

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Transforma cada rol en una autoridad con prefijo "ROLE_"
        return roles.stream()
                .map(r -> (GrantedAuthority) () -> "ROLE_" + r.getName())
                .collect(Collectors.toSet());
    }

    // Métodos requeridos por UserDetails para el control de la cuenta
    @Override public boolean isAccountNonExpired() { return true; } // La cuenta nunca expira
    @Override public boolean isAccountNonLocked() { return true; } // La cuenta nunca se bloquea
    @Override public boolean isCredentialsNonExpired() { return true; } // Las credenciales nunca expiran
    @Override public boolean isEnabled() { return activo; } // El usuario está habilitado si activo es true
}
