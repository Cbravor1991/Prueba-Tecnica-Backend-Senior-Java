package com.gestion.fintech.controller;

import com.gestion.fintech.dto.ReporteFinancieroDTO;
import com.gestion.fintech.dto.TransaccionCuentaDTO;
import com.gestion.fintech.dto.TransaccionInterCuentaDTO;
import com.gestion.fintech.exception.TransaccionException;
import com.gestion.fintech.model.Cuenta;
import com.gestion.fintech.model.Transaccion;
import com.gestion.fintech.model.Usuario;
import com.gestion.fintech.service.TransaccionService;
import com.gestion.fintech.service.UsuarioService;
import com.gestion.fintech.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Gestión de Transacciones", description = "Operaciones para realizar depósitos, retiros, transferencias y consultar transacciones. Todos los endpoints requieren autenticación con token JWT.")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    private <T> T getFutureResult(CompletableFuture<T> future) {
        return future.join();
    }

    @Operation(summary = "Realizar un depósito", description = "Permite realizar un depósito en una cuenta específica. Requiere autenticación con token JWT.")
    @PostMapping("/deposito/{cuentaId}")
    public ResponseEntity<Transaccion> realizarDeposito(@PathVariable Long cuentaId, @RequestBody TransaccionInterCuentaDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new TransaccionException("Usuario no encontrado");
        }
        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta cuenta = transaccionService.obtenerCuentaPorId(cuentaId);
        if (!cuenta.getTitular().equals(nombreTitular)) {
            throw new TransaccionException("Acceso denegado: el usuario no es el titular de la cuenta");
        }

        Transaccion transaccion = getFutureResult(transaccionService.realizarDeposito(cuentaId, request.getMonto()));
        return ResponseEntity.ok(transaccion);
    }

    @Operation(summary = "Realizar un retiro", description = "Permite realizar un retiro de una cuenta específica. Requiere autenticación con token JWT.")
    @PostMapping("/retiro/{cuentaId}")
    public ResponseEntity<Transaccion> realizarRetiro(@PathVariable Long cuentaId, @RequestBody TransaccionInterCuentaDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new TransaccionException("Usuario no encontrado");
        }
        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta cuenta = transaccionService.obtenerCuentaPorId(cuentaId);
        if (!cuenta.getTitular().equals(nombreTitular)) {
            throw new TransaccionException("Acceso denegado: el usuario no es el titular de la cuenta");
        }

        Transaccion transaccion = getFutureResult(transaccionService.realizarRetiro(cuentaId, request.getMonto()));
        return ResponseEntity.ok(transaccion);
    }

    @Operation(summary = "Realizar una transferencia", description = "Permite realizar una transferencia entre cuentas. Requiere autenticación con token JWT.")
    @PostMapping("/transferencia/{cuentaOrigenId}")
    public ResponseEntity<Transaccion> realizarTransferencia(@PathVariable Long cuentaOrigenId, @RequestBody TransaccionCuentaDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new TransaccionException("Usuario no encontrado");
        }
        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta cuentaOrigen = transaccionService.obtenerCuentaPorId(cuentaOrigenId);
        if (!cuentaOrigen.getTitular().equals(nombreTitular)) {
            throw new TransaccionException("Acceso denegado: el usuario no es el titular de la cuenta");
        }

        Transaccion transaccion = getFutureResult(transaccionService.realizarTransferencia(cuentaOrigenId, request.getCuentaDestinoId(), request.getMonto()));
        return ResponseEntity.ok(transaccion);
    }

    @Operation(summary = "Obtener historial de transacciones", description = "Permite consultar el historial de transacciones de una cuenta específica. Requiere autenticación con token JWT.")
    @GetMapping("/historial/{cuentaId}")
    public ResponseEntity<List<Transaccion>> getHistorial(@PathVariable Long cuentaId,
                                                          @RequestParam(required = false) String tipo,
                                                          @RequestParam(required = false) LocalDateTime fechaDesde,
                                                          @RequestParam(required = false) LocalDateTime fechaHasta,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new TransaccionException("Usuario no encontrado");
        }

        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta cuenta = transaccionService.obtenerCuentaPorId(cuentaId);
        if (!cuenta.getTitular().equals(nombreTitular)) {
            throw new TransaccionException("Acceso denegado: el usuario no es el titular de la cuenta");
        }

        List<Transaccion> historial = getFutureResult(transaccionService.obtenerHistorial(cuentaId, tipo, fechaDesde, fechaHasta, page, size));
        return ResponseEntity.ok(historial);
    }

    @Operation(summary = "Generar reporte financiero", description = "Genera un reporte financiero de una cuenta específica dentro de un rango de fechas. Requiere autenticación con token JWT.")
    @GetMapping("/reportes/{cuentaId}")
    public ResponseEntity<ReporteFinancieroDTO> generarReporte(@PathVariable Long cuentaId,
                                                               @RequestParam LocalDateTime fechaDesde,
                                                               @RequestParam LocalDateTime fechaHasta) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new TransaccionException("Usuario no encontrado");
        }
        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta cuenta = transaccionService.obtenerCuentaPorId(cuentaId);
        if (!cuenta.getTitular().equals(nombreTitular)) {
            throw new TransaccionException("Acceso denegado: el usuario no es el titular de la cuenta");
        }

        ReporteFinancieroDTO reporte = getFutureResult(transaccionService.generarReporteFinanciero(cuentaId, fechaDesde, fechaHasta));
        return ResponseEntity.ok(reporte);
    }
}
