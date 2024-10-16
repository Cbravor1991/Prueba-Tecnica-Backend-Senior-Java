package com.gestion.fintech.service;

import com.gestion.fintech.model.Cuenta;
import com.gestion.fintech.model.Transaccion;
import com.gestion.fintech.dto.ReporteFinancieroDTO;
import com.gestion.fintech.repository.CuentaRepository;
import com.gestion.fintech.repository.TransaccionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gestion.fintech.exception.TransaccionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransaccionService {

    private static final Logger logger = LoggerFactory.getLogger(TransaccionService.class);

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @PostConstruct
    public void init() {
        // Imprimir la implementación de SLF4J
        System.out.println("SLF4J Logger Implementation: " + LoggerFactory.getILoggerFactory().getClass());
    }


    public Cuenta obtenerCuentaPorId(Long cuentaId) {
        logger.info("Obteniendo cuenta con ID: {}", cuentaId);

        return cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> {
                    logger.error("Cuenta no encontrada: ID {}", cuentaId);
                    return new RuntimeException("Cuenta no encontrada");
                });
    }

    @Transactional
    public Transaccion realizarDeposito(Long cuentaId, BigDecimal monto) {
        logger.info("Iniciando depósito en cuenta ID: {}, monto: {}", cuentaId, monto);
        Cuenta cuenta = obtenerCuentaPorId(cuentaId);

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Error en el depósito: el monto debe ser positivo.");
            throw new TransaccionException("El monto debe ser positivo");
        }

        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        cuentaRepository.save(cuenta);

        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaOrigenId(cuentaId);
        transaccion.setMonto(monto);
        transaccion.setTipo("DEPOSITO");
        transaccion.setFecha(LocalDateTime.now());

        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        logger.info("Depósito exitoso: cuenta ID: {}, monto: {}", cuentaId, monto);
        return transaccionGuardada;
    }

    @Transactional
    public Transaccion realizarRetiro(Long cuentaId, BigDecimal monto) {
        logger.info("Iniciando retiro en cuenta ID: {}, monto: {}", cuentaId, monto);
        Cuenta cuenta = obtenerCuentaPorId(cuentaId);

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Error en el retiro: el monto debe ser positivo.");
            throw new TransaccionException("El monto debe ser positivo");
        }
        if (cuenta.getSaldo().compareTo(monto) < 0) {
            logger.error("Error en el retiro: saldo insuficiente en cuenta ID: {}", cuentaId);
            throw new TransaccionException("Saldo insuficiente");
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
        cuentaRepository.save(cuenta);

        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaOrigenId(cuentaId);
        transaccion.setMonto(monto);
        transaccion.setTipo("RETIRO");
        transaccion.setFecha(LocalDateTime.now());

        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        logger.info("Retiro exitoso: cuenta ID: {}, monto: {}", cuentaId, monto);
        return transaccionGuardada;
    }

    @Transactional
    public Transaccion realizarTransferencia(Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal monto) {
        logger.info("Iniciando transferencia de la cuenta ID: {} a la cuenta ID: {}, monto: {}", cuentaOrigenId, cuentaDestinoId, monto);
        Cuenta cuentaOrigen = obtenerCuentaPorId(cuentaOrigenId);
        Cuenta cuentaDestino = obtenerCuentaPorId(cuentaDestinoId);

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Error en la transferencia: el monto debe ser positivo.");
            throw new TransaccionException("El monto debe ser positivo");
        }
        if (cuentaOrigen.getSaldo().compareTo(monto) < 0) {
            logger.error("Error en la transferencia: saldo insuficiente en la cuenta origen ID: {}", cuentaOrigenId);
            throw new TransaccionException("Saldo insuficiente en la cuenta origen");
        }
        if (!cuentaOrigen.getMoneda().equals(cuentaDestino.getMoneda())) {
            logger.error("Error en la transferencia: monedas distintas entre cuenta origen ID: {} y cuenta destino ID: {}", cuentaOrigenId, cuentaDestinoId);
            throw new TransaccionException("Las cuentas deben tener el mismo tipo de moneda para realizar la transferencia");
        }

        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(monto));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(monto));

        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);

        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaOrigenId(cuentaOrigenId);
        transaccion.setCuentaDestinoId(cuentaDestinoId);
        transaccion.setMonto(monto);
        transaccion.setTipo("TRANSFERENCIA");
        transaccion.setFecha(LocalDateTime.now());

        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        logger.info("Transferencia exitosa de la cuenta ID: {} a la cuenta ID: {}, monto: {}", cuentaOrigenId, cuentaDestinoId, monto);
        return transaccionGuardada;
    }

    @Transactional(readOnly = true)
    public List<Transaccion> obtenerHistorial(Long cuentaId, String tipo, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        logger.info("Obteniendo historial de transacciones para la cuenta ID: {}, tipo: {}, desde: {}, hasta: {}", cuentaId, tipo, fechaDesde, fechaHasta);
        List<Transaccion> transacciones = transaccionRepository.findByCuentaOrigenIdAndTipoAndFechaBetween(cuentaId, tipo, fechaDesde, fechaHasta);
        logger.info("Historial obtenido: {} transacciones encontradas", transacciones.size());
        return transacciones;
    }

    @Transactional(readOnly = true)
    public ReporteFinancieroDTO generarReporteFinanciero(Long cuentaId, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        logger.info("Generando reporte financiero para la cuenta ID: {}, desde: {}, hasta: {}", cuentaId, fechaDesde, fechaHasta);
        Cuenta cuenta = obtenerCuentaPorId(cuentaId);
        List<Transaccion> transacciones = transaccionRepository.findByCuentaOrigenIdAndFechaBetween(cuentaId, fechaDesde, fechaHasta);

        BigDecimal saldoInicial = cuenta.getSaldo();
        BigDecimal totalDepositos = transacciones.stream()
                .filter(t -> t.getTipo().equals("DEPOSITO"))
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRetiros = transacciones.stream()
                .filter(t -> t.getTipo().equals("RETIRO"))
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoFinal = saldoInicial.add(totalDepositos).subtract(totalRetiros);

        ReporteFinancieroDTO reporte = new ReporteFinancieroDTO();
        reporte.setSaldoInicial(saldoInicial);
        reporte.setTotalDepositos(totalDepositos);
        reporte.setTotalRetiros(totalRetiros);
        reporte.setSaldoFinal(saldoFinal);
        reporte.setFechaDesde(fechaDesde);
        reporte.setFechaHasta(fechaHasta);

        logger.info("Reporte financiero generado exitosamente para la cuenta ID: {}", cuentaId);
        return reporte;
    }
}
