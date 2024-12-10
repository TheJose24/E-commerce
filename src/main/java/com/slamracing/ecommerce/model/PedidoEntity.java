package com.slamracing.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.slamracing.ecommerce.model.enums.EstadoPedido;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "pedidos")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString(exclude = "detalles")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_id")
    private Long pedidoId;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private UsuarioEntity usuario;

    @Column(name = "fecha_pedido", updatable = false)
    @CreatedDate
    private LocalDateTime fechaPedido;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    private BigDecimal subtotal;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(name = "ultima_actualizacion")
    @LastModifiedDate
    private LocalDateTime ultimaActualizacion;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<DetallePedidoEntity> detalles;
}
