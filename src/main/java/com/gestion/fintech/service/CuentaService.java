package com.gestion.fintech.service;

import com.gestion.fintech.exception.CuentaException; // Importar CuentaException
import com.gestion.fintech.model.Cuenta;
import com.gestion.fintech.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@Service
public class CuentaService {

    private static final Logger logger = LoggerFactory.getLogger(CuentaService.class);

    @Autowired
    private CuentaRepository cuentaRepository;

    @Async
    @Transactional
    public CompletableFuture<Cuenta> crearCuenta(Cuenta cuenta) {
        logger.info("Creando nueva cuenta para el titular: {}", cuenta.getTitular());

        cuenta.setNumeroCuenta("CUENTA-" + System.currentTimeMillis());
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);

        logger.info("Cuenta creada exitosamente con ID: {}", cuentaGuardada.getId());
        return CompletableFuture.completedFuture(cuentaGuardada);
    }

    @Async
    @Transactional
    public CompletableFuture<Cuenta> actualizarCuenta(Long id, Cuenta cuenta) {
        logger.info("Actualizando cuenta con ID: {}", id);

        Cuenta cuentaExistente = cuentaRepository.findById(id)
                .orElseThrow(() -> {

                    return new CuentaException("Cuenta no encontrada");
                });

        cuentaExistente.setTitular(cuenta.getTitular());
        cuentaExistente.setMoneda(cuenta.getMoneda());
        cuentaExistente.setSaldo(cuenta.getSaldo());

        Cuenta cuentaActualizada = cuentaRepository.save(cuentaExistente);
        logger.info("Cuenta actualizada exitosamente con ID: {}", id);

        return CompletableFuture.completedFuture(cuentaActualizada);
    }

    @Async
    @Transactional
    public CompletableFuture<Void> eliminarCuenta(Long id) {
        logger.info("Eliminando cuenta con ID: {}", id);

        cuentaRepository.deleteById(id);
        logger.info("Cuenta eliminada exitosamente con ID: {}", id);
        return CompletableFuture.completedFuture(null);
    }

    public Cuenta obtenerCuentaPorId(Long id) {
        logger.info("Obteniendo cuenta con ID: {}", id);

        return cuentaRepository.findById(id)
                .orElseThrow(() -> {

                    return new CuentaException("Cuenta no encontrada");
                });
    }
}
