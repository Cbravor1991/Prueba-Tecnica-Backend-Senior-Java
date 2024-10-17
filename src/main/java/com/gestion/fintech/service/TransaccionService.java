package com.gestion.fintech.service;

import com.gestion.fintech.exception.CuentaException;
import com.gestion.fintech.model.Cuenta;
import com.gestion.fintech.model.Transaccion;
import com.gestion.fintech.dto.ReporteFinancieroDTO;
import com.gestion.fintech.repository.CuentaRepository;
import com.gestion.fintech.repository.TransaccionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gestion.fintech.exception.TransaccionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TransaccionService {

    private static final Logger logger = LoggerFactory.getLogger(TransaccionService.class);

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @PostConstruct
    public void init() {
        System.out.println("SLF4J Logger Implementation: " + LoggerFactory.getILoggerFactory().getClass());
    }

    public Cuenta obtenerCuentaPorId(Long cuentaId) {
        logger.info("Obteniendo cuenta con ID: {}", cuentaId);
        return cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> {

                    return new CuentaException("Cuenta no encontrada");
                });
    }

    @Async
    @Transactional
    public CompletableFuture<Transaccion> realizarDeposito(Long cuentaId, BigDecimal monto) {
        logger.info("Iniciando depósito en cuenta ID: {}, monto: {}", cuentaId, monto);
        Cuenta cuenta = obtenerCuentaPorId(cuentaId);

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
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
        return CompletableFuture.completedFuture(transaccionGuardada);
    }

    @Async
    @Transactional
    public CompletableFuture<Transaccion> realizarRetiro(Long cuentaId, BigDecimal monto) {
        logger.info("Iniciando retiro en cuenta ID: {}, monto: {}", cuentaId, monto);
        Cuenta cuenta = obtenerCuentaPorId(cuentaId);

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransaccionException("El monto debe ser positivo");
        }
        if (cuenta.getSaldo().compareTo(monto) < 0) {
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
        return CompletableFuture.completedFuture(transaccionGuardada);
    }

    @Async
    @Transactional
    public CompletableFuture<Transaccion> realizarTransferencia(Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal monto) {
        logger.info("Iniciando transferencia de la cuenta ID: {} a la cuenta ID: {}, monto: {}", cuentaOrigenId, cuentaDestinoId, monto);

            Cuenta cuentaOrigen = obtenerCuentaPorId(cuentaOrigenId);

            Cuenta cuentaDestino = obtenerCuentaPorId(cuentaDestinoId);


            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                throw new TransaccionException("El monto debe ser positivo");
            }
            if (cuentaOrigen.getSaldo().compareTo(monto) < 0) {
                throw new TransaccionException("Saldo insuficiente en la cuenta origen");
            }
            if (!cuentaOrigen.getMoneda().equals(cuentaDestino.getMoneda())) {
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
            return CompletableFuture.completedFuture(transaccionGuardada);


    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<List<Transaccion>> obtenerHistorial(Long cuentaId, String tipo, LocalDateTime fechaDesde, LocalDateTime fechaHasta, int page, int size) {
        logger.info("Obteniendo historial de transacciones para la cuenta ID: {}, tipo: {}, desde: {}, hasta: {}, página: {}, tamaño: {}", cuentaId, tipo, fechaDesde, fechaHasta, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaccion> transacciones = transaccionRepository.findByCuentaOrigenIdAndTipoAndFechaBetween(cuentaId, tipo, fechaDesde, fechaHasta, pageable);
        logger.info("Historial obtenido: {} transacciones encontradas", transacciones.getTotalElements());

        return CompletableFuture.completedFuture(transacciones.getContent());
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<ReporteFinancieroDTO> generarReporteFinanciero(Long cuentaId, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        logger.info("Generando reporte financiero para la cuenta ID: {}, desde: {}, hasta: {}", cuentaId, fechaDesde, fechaHasta);

        Cuenta cuenta = obtenerCuentaPorId(cuentaId);

        BigDecimal totalDepositos = transaccionRepository.sumarMontosPorTipo(cuentaId, "DEPOSITO", fechaDesde, fechaHasta);
        BigDecimal totalRetiros = transaccionRepository.sumarMontosPorTipo(cuentaId, "RETIRO", fechaDesde, fechaHasta);

        totalDepositos = totalDepositos != null ? totalDepositos : BigDecimal.ZERO;
        totalRetiros = totalRetiros != null ? totalRetiros : BigDecimal.ZERO;

        BigDecimal saldoInicial = cuenta.getSaldo();
        BigDecimal saldoFinal = saldoInicial.add(totalDepositos).subtract(totalRetiros);

        ReporteFinancieroDTO reporte = new ReporteFinancieroDTO();
        reporte.setSaldoInicial(saldoInicial);
        reporte.setTotalDepositos(totalDepositos);
        reporte.setTotalRetiros(totalRetiros);
        reporte.setSaldoFinal(saldoFinal);
        reporte.setFechaDesde(fechaDesde);
        reporte.setFechaHasta(fechaHasta);

        logger.info("Reporte financiero generado: {}", reporte);
        return CompletableFuture.completedFuture(reporte);
    }
}
