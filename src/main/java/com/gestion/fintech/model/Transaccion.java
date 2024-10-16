package com.gestion.fintech.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(
        indexes = {
                @Index(name = "idx_cuenta_origen_id", columnList = "cuenta_origen_id"),
                @Index(name = "idx_cuenta_destino_id", columnList = "cuenta_destino_id"),
                @Index(name = "idx_tipo", columnList = "tipo"),
                @Index(name = "idx_fecha", columnList = "fecha")
        }
)
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuenta_origen_id")
    private Long cuentaOrigenId;

    @Column(name = "cuenta_destino_id")
    private Long cuentaDestinoId;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private String tipo; // "DEPOSITO", "RETIRO", "TRANSFERENCIA"

    @Column(nullable = false)
    private LocalDateTime fecha;
}
