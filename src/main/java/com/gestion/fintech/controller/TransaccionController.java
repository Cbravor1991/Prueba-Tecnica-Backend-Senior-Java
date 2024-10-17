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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    private <T> T getFutureResult(CompletableFuture<T> future) {
        try {
            return future.join();
        } catch (Exception e) {

            throw new RuntimeException("Error al obtener el resultado del futuro: " + e.getMessage(), e);
        }
    }

    @PostMapping("/deposito/{cuentaId}")
    public ResponseEntity<Transaccion> realizarDeposito(@PathVariable Long cuentaId, @RequestBody TransaccionInterCuentaDTO request) {
        try {
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

            Transaccion transaccion = getFutureResult(transaccionService.realizarDeposito(cuentaId, request.getMonto()));
            return ResponseEntity.ok(transaccion);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/retiro/{cuentaId}")
    public ResponseEntity<Transaccion> realizarRetiro(@PathVariable Long cuentaId, @RequestBody TransaccionInterCuentaDTO request) {
        try {
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

            Transaccion transaccion = getFutureResult(transaccionService.realizarRetiro(cuentaId, request.getMonto()));
            return ResponseEntity.ok(transaccion);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/transferencia/{cuentaOrigenId}")
    public ResponseEntity<Transaccion> realizarTransferencia(@PathVariable Long cuentaOrigenId, @RequestBody TransaccionCuentaDTO request) {
        try {
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

            Transaccion transaccion = getFutureResult(transaccionService.realizarTransferencia(cuentaOrigenId, request.getCuentaDestinoId(), request.getMonto()));
            return ResponseEntity.ok(transaccion);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/historial/{cuentaId}")
    public ResponseEntity<List<Transaccion>> getHistorial(@PathVariable Long cuentaId,
                                                          @RequestParam(required = false) String tipo,
                                                          @RequestParam(required = false) LocalDateTime fechaDesde,
                                                          @RequestParam(required = false) LocalDateTime fechaHasta,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        try {
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

            List<Transaccion> historial = getFutureResult(transaccionService.obtenerHistorial(cuentaId, tipo, fechaDesde, fechaHasta, page, size));
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/reportes/{cuentaId}")
    public ResponseEntity<ReporteFinancieroDTO> generarReporte(@PathVariable Long cuentaId,
                                                               @RequestParam LocalDateTime fechaDesde,
                                                               @RequestParam LocalDateTime fechaHasta) {
        try {
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


            ReporteFinancieroDTO reporte = getFutureResult(transaccionService.generarReporteFinanciero(cuentaId, fechaDesde, fechaHasta));
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
