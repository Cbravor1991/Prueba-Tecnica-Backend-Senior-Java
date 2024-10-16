package com.gestion.fintech.service;

import com.gestion.fintech.model.Cuenta;
import com.gestion.fintech.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CuentaService {


    private static final Logger logger = LoggerFactory.getLogger(CuentaService.class);

    @Autowired
    private CuentaRepository cuentaRepository;

    @Transactional
    public Cuenta crearCuenta(Cuenta cuenta) {
        logger.info("Creando nueva cuenta para el titular: {}", cuenta.getTitular());
        System.out.println("llege aca");


        cuenta.setNumeroCuenta("CUENTA-" + System.currentTimeMillis());
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);

        logger.info("Cuenta creada exitosamente con ID: {}", cuentaGuardada.getId());
        return cuentaGuardada;
    }

    @Transactional
    public Cuenta actualizarCuenta(Long id, Cuenta cuenta) {
        logger.info("Actualizando cuenta con ID: {}", id);

        Cuenta cuentaExistente = cuentaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cuenta no encontrada con ID: {}", id);
                    return new RuntimeException("Cuenta no encontrada");
                });

        cuentaExistente.setTitular(cuenta.getTitular());
        cuentaExistente.setMoneda(cuenta.getMoneda());

        Cuenta cuentaActualizada = cuentaRepository.save(cuentaExistente);
        logger.info("Cuenta actualizada exitosamente con ID: {}", id);

        return cuentaActualizada;
    }

    @Transactional
    public void eliminarCuenta(Long id) {
        logger.info("Eliminando cuenta con ID: {}", id);

        cuentaRepository.deleteById(id);
        logger.info("Cuenta eliminada exitosamente con ID: {}", id);
    }

    public Cuenta obtenerCuentaPorId(Long id) {
        logger.info("Obteniendo cuenta con ID: {}", id);

        return cuentaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cuenta no encontrada con ID: {}", id);
                    return new RuntimeException("Cuenta no encontrada");
                });
    }
}
