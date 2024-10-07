package com.gestion.fintech.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroCuenta;

    @Column(nullable = false)
    private String titular;

    @Column(nullable = false)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(nullable = false)
    private String moneda;


}
