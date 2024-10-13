package com.gestion.fintech.controller;

import com.gestion.fintech.dto.ReporteFinancieroDTO;
import com.gestion.fintech.dto.TransaccionCuentaDTO;
import com.gestion.fintech.dto.TransaccionInterCuentaDTO;
import com.gestion.fintech.model.Cuenta;
import com.gestion.fintech.model.Transaccion;
import com.gestion.fintech.model.Usuario;
import com.gestion.fintech.service.TransaccionService;
import com.gestion.fintech.service.UsuarioService;
import com.gestion.fintech.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/deposito/{cuentaId}")
    public ResponseEntity<Transaccion> realizarDeposito(@PathVariable Long cuentaId, @RequestBody TransaccionInterCuentaDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta cuenta = transaccionService.obtenerCuentaPorId(cuentaId);
        if (!cuenta.getTitular().equals(nombreTitular)) {
            return ResponseEntity.status(403).body(null);
        }
        Transaccion transaccion = transaccionService.realizarDeposito(cuentaId, request.getMonto());
        return ResponseEntity.ok(transaccion);
    }

    @PostMapping("/retiro/{cuentaId}")
    public ResponseEntity<Transaccion> realizarRetiro(@PathVariable Long cuentaId, @RequestBody TransaccionInterCuentaDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta cuenta = transaccionService.obtenerCuentaPorId(cuentaId);
        if (!cuenta.getTitular().equals(nombreTitular)) {
            return ResponseEntity.status(403).body(null);
        }
        Transaccion transaccion = transaccionService.realizarRetiro(cuentaId, request.getMonto());
        return ResponseEntity.ok(transaccion);
    }

    @PostMapping("/transferencia/{cuentaOrigenId}")
    public ResponseEntity<Transaccion> realizarTransferencia(@PathVariable Long cuentaOrigenId, @RequestBody TransaccionCuentaDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta cuentaOrigen = transaccionService.obtenerCuentaPorId(cuentaOrigenId);
        if (!cuentaOrigen.getTitular().equals(nombreTitular)) {
            return ResponseEntity.status(403).body(null);
        }
        Transaccion transaccion = transaccionService.realizarTransferencia(cuentaOrigenId, request.getCuentaDestinoId(), request.getMonto());
        return ResponseEntity.ok(transaccion);
    }

    @GetMapping("/historial/{cuentaId}")
    public ResponseEntity<List<Transaccion>> getHistorial(@PathVariable Long cuentaId,
                                                          @RequestParam(required = false) String tipo,
                                                          @RequestParam(required = false) LocalDateTime fechaDesde,
                                                          @RequestParam(required = false) LocalDateTime fechaHasta) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta cuenta = transaccionService.obtenerCuentaPorId(cuentaId);
        if (!cuenta.getTitular().equals(nombreTitular)) {
            return ResponseEntity.status(403).body(null);
        }
        List<Transaccion> historial = transaccionService.obtenerHistorial(cuentaId, tipo, fechaDesde, fechaHasta);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/reportes/{cuentaId}")
    public ResponseEntity<ReporteFinancieroDTO> generarReporte(@PathVariable Long cuentaId,
                                                               @RequestParam LocalDateTime fechaDesde,
                                                               @RequestParam LocalDateTime fechaHasta) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta cuenta = transaccionService.obtenerCuentaPorId(cuentaId);
        if (!cuenta.getTitular().equals(nombreTitular)) {
            return ResponseEntity.status(403).body(null);
        }
        ReporteFinancieroDTO reporte = transaccionService.generarReporteFinanciero(cuentaId, fechaDesde, fechaHasta);
        return ResponseEntity.ok(reporte);
    }
}
