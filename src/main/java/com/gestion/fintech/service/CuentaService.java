package com.gestion.fintech.service;

import com.gestion.fintech.model.Cuenta;
import com.gestion.fintech.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    public Cuenta crearCuenta(Cuenta cuenta) {
        cuenta.setNumeroCuenta("CUENTA-" + System.currentTimeMillis());
        return cuentaRepository.save(cuenta);
    }

    public Cuenta actualizarCuenta(Long id, Cuenta cuenta) {
        Cuenta cuentaExistente = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        cuentaExistente.setTitular(cuenta.getTitular());
        cuentaExistente.setMoneda(cuenta.getMoneda());
        return cuentaRepository.save(cuentaExistente);
    }

    public void eliminarCuenta(Long id) {
        cuentaRepository.deleteById(id);
    }


    public Cuenta obtenerCuentaPorId(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
    }
}
