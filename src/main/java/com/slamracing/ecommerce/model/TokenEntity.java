package com.slamracing.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@ToString(exclude = "usuario")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TokenType tokenType = TokenType.BEARER;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRevoked = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isExpired = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonBackReference("usuario-tokens")
    private UsuarioEntity usuario;

    public enum TokenType {
        BEARER
    }
}
