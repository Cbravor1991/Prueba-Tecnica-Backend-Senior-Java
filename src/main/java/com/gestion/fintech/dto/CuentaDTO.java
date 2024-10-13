package com.gestion.fintech.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CuentaDTO {
    private BigDecimal saldo;
    private String moneda;
}
