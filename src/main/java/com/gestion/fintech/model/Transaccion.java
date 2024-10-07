package com.gestion.fintech.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cuentaOrigenId;
    private Long cuentaDestinoId;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private String tipo; // "DEPOSITO", "RETIRO", "TRANSFERENCIA"

    @Column(nullable = false)
    private LocalDateTime fecha;
}