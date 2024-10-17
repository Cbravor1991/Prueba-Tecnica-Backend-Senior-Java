package com.gestion.fintech.controller;

import com.gestion.fintech.dto.CuentaDTO;
import com.gestion.fintech.model.Cuenta;
import com.gestion.fintech.model.Usuario;
import com.gestion.fintech.service.CuentaService;
import com.gestion.fintech.service.UsuarioService;
import com.gestion.fintech.exception.CuentaException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/cuentas")
@Tag(name = "Gestión de Cuentas", description = "Operaciones relacionadas con la creación, modificación y eliminación de cuentas.")
public class CuentaController {
    private static final Logger logger = LoggerFactory.getLogger(CuentaController.class);

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private UsuarioService usuarioService;

    private <T> T getFutureResult(CompletableFuture<T> future) {
        return future.join();
    }

    @PostMapping
    @Operation(summary = "Crear una nueva cuenta", description = "Crea una nueva cuenta para el usuario autenticado con los datos proporcionados.")
    public ResponseEntity<Cuenta> crearCuenta(@RequestBody CuentaDTO cuentaDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);

        if (usuarioOpt.isEmpty()) {
            throw new CuentaException("Usuario no encontrado");
        }

        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setTitular(nombreTitular);
        nuevaCuenta.setSaldo(cuentaDTO.getSaldo());
        nuevaCuenta.setMoneda(cuentaDTO.getMoneda());
        nuevaCuenta.setNumeroCuenta("NUMERO_AUTOGENERADO");

        Cuenta cuentaCreada = getFutureResult(cuentaService.crearCuenta(nuevaCuenta));
        return ResponseEntity.ok(cuentaCreada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una cuenta", description = "Actualiza los datos de una cuenta existente para el usuario autenticado.")
    public ResponseEntity<Cuenta> actualizarCuenta(@PathVariable Long id, @RequestBody CuentaDTO cuentaDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);

        if (usuarioOpt.isEmpty()) {
            throw new CuentaException("Usuario no encontrado");
        }

        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta cuentaExistente = cuentaService.obtenerCuentaPorId(id);

        if (!cuentaExistente.getTitular().equals(nombreTitular)) {
            throw new CuentaException("No tiene permiso para actualizar esta cuenta");
        }

        cuentaExistente.setSaldo(cuentaDTO.getSaldo());
        cuentaExistente.setMoneda(cuentaDTO.getMoneda());

        Cuenta cuentaActualizada = getFutureResult(cuentaService.actualizarCuenta(id, cuentaExistente));
        return ResponseEntity.ok(cuentaActualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una cuenta", description = "Elimina una cuenta existente si pertenece al usuario autenticado.")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);

        if (usuarioOpt.isEmpty()) {
            throw new CuentaException("Usuario no encontrado");
        }

        String nombreTitular = usuarioOpt.get().getNombreTitular();
        Cuenta cuentaExistente = cuentaService.obtenerCuentaPorId(id);

        if (!cuentaExistente.getTitular().equals(nombreTitular)) {
            throw new CuentaException("No tiene permiso para eliminar esta cuenta");
        }

        cuentaService.eliminarCuenta(id);
        return ResponseEntity.noContent().build();
    }
}
