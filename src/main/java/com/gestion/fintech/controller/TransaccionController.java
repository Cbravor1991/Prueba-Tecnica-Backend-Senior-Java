package com.gestion.fintech.controller;

import com.gestion.fintech.dto.ReporteFinancieroDTO;
import com.gestion.fintech.model.Transaccion;
import com.gestion.fintech.service.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @PostMapping("/deposito/{cuentaId}")
    public ResponseEntity<Transaccion> realizarDeposito(@PathVariable Long cuentaId, @RequestParam BigDecimal monto) {
        Transaccion transaccion = transaccionService.realizarDeposito(cuentaId, monto);
        return ResponseEntity.ok(transaccion);
    }

    @PostMapping("/retiro/{cuentaId}")
    public ResponseEntity<Transaccion> realizarRetiro(@PathVariable Long cuentaId, @RequestParam BigDecimal monto) {
        Transaccion transaccion = transaccionService.realizarRetiro(cuentaId, monto);
        return ResponseEntity.ok(transaccion);
    }

    @PostMapping("/transferencia")
    public ResponseEntity<Transaccion> realizarTransferencia(@RequestParam Long cuentaOrigenId,
                                                             @RequestParam Long cuentaDestinoId,
                                                             @RequestParam BigDecimal monto) {
        Transaccion transaccion = transaccionService.realizarTransferencia(cuentaOrigenId, cuentaDestinoId, monto);
        return ResponseEntity.ok(transaccion);
    }

    @GetMapping("/historial/{cuentaId}")
    public ResponseEntity<List<Transaccion>> getHistorial(@PathVariable Long cuentaId,
                                                          @RequestParam(required = false) String tipo,
                                                          @RequestParam(required = false) LocalDateTime fechaDesde,
                                                          @RequestParam(required = false) LocalDateTime fechaHasta) {
        List<Transaccion> historial = transaccionService.obtenerHistorial(cuentaId, tipo, fechaDesde, fechaHasta);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/reportes/{cuentaId}")
    public ResponseEntity<ReporteFinancieroDTO> generarReporte(@PathVariable Long cuentaId,
                                                            @RequestParam LocalDateTime fechaDesde,
                                                            @RequestParam LocalDateTime fechaHasta) {
        ReporteFinancieroDTO reporte = transaccionService.generarReporteFinanciero(cuentaId, fechaDesde, fechaHasta);
        return ResponseEntity.ok(reporte);
    }

}
