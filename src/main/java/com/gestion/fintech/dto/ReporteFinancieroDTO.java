package com.gestion.fintech.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReporteFinancieroDTO {
    private BigDecimal saldoInicial;
    private BigDecimal totalDepositos;
    private BigDecimal totalRetiros;
    private BigDecimal saldoFinal;
    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
}
