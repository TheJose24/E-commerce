package com.slamracing.ecommerce.repository;

import com.slamracing.ecommerce.model.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    @Query(value = """
        select t from TokenEntity t inner join UsuarioEntity u
        on t.usuario.usuarioId = u.usuarioId
        where u.usuarioId = :id and (t.isExpired = false and t.isRevoked = false)
        """)
    List<TokenEntity> findAllValidTokenByUser(@Param("id") Long id);

    @Query("SELECT t FROM TokenEntity t " +
            "WHERE t.usuario.usuarioId = :userId AND t.isExpired = false AND t.isRevoked = false")
    Optional<TokenEntity> findValidTokenByUsuario(@Param("userId") Long userId);

    Optional<TokenEntity> findByToken(String token);
}
