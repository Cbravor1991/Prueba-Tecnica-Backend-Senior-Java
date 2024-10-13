package com.gestion.fintech.controller;

import com.gestion.fintech.dto.CuentaDTO;
import com.gestion.fintech.model.Cuenta;
import com.gestion.fintech.model.Usuario;
import com.gestion.fintech.service.CuentaService;
import com.gestion.fintech.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Cuenta> crearCuenta(@RequestBody CuentaDTO cuentaDTO) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }


        String nombreTitular = usuarioOpt.get().getNombreTitular();


        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setTitular(nombreTitular);
        nuevaCuenta.setSaldo(cuentaDTO.getSaldo());
        nuevaCuenta.setMoneda(cuentaDTO.getMoneda());


        nuevaCuenta.setNumeroCuenta("NUMERO_AUTOGENERADO");


        Cuenta cuentaCreada = cuentaService.crearCuenta(nuevaCuenta);
        return ResponseEntity.ok(cuentaCreada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> actualizarCuenta(@PathVariable Long id, @RequestBody CuentaDTO cuentaDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Cuenta cuentaExistente = cuentaService.obtenerCuentaPorId(id);


        if (!cuentaExistente.getTitular().equals(username)) {
            return ResponseEntity.status(403).body(null);
        }


        cuentaExistente.setSaldo(cuentaDTO.getSaldo());
        cuentaExistente.setMoneda(cuentaDTO.getMoneda());

        Cuenta cuentaActualizada = cuentaService.actualizarCuenta(id, cuentaExistente);
        return ResponseEntity.ok(cuentaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Cuenta cuentaExistente = cuentaService.obtenerCuentaPorId(id);


        if (!cuentaExistente.getTitular().equals(username)) {
            return ResponseEntity.status(403).build();
        }

        cuentaService.eliminarCuenta(id);
        return ResponseEntity.noContent().build();
    }
}
