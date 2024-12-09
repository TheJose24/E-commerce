package com.slamracing.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.slamracing.ecommerce.model.enums.EstadoUsuario;
import com.slamracing.ecommerce.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@ToString(exclude = {"tokens", "direcciones"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long usuarioId;

    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonManagedReference("usuario-tokens")
    private List<TokenEntity> tokens;

    @Column(name = "fecha_registro", updatable = false)
    @CreatedDate
    private LocalDateTime fechaRegistro;

    @Column(name = "ultima_actualizacion")
    @LastModifiedDate
    private LocalDateTime ultimaActualizacion;

    @Column(name = "token_recuperacion")
    private String tokenRecuperacion;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DireccionEntity> direcciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_usuario")
    private EstadoUsuario estadoUsuario;

    @OneToMany(mappedBy = "usuario")
    private List<PedidoEntity> pedidos;

}
