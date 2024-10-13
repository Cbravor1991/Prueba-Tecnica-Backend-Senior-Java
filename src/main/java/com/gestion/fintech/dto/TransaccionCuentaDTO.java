package com.gestion.fintech.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransaccionCuentaDTO {
    private BigDecimal monto;
    private Long cuentaDestinoId;
}
