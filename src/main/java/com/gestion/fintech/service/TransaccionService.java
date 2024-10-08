package com.gestion.fintech.service;

import com.gestion.fintech.model.Cuenta;
import com.gestion.fintech.model.Transaccion;
import com.gestion.fintech.dto.ReporteFinancieroDTO;
import com.gestion.fintech.repository.CuentaRepository;
import com.gestion.fintech.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransaccionService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Transactional
    public Transaccion realizarDeposito(Long cuentaId, BigDecimal monto) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El monto no puede ser negativo");
        }

        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        cuentaRepository.save(cuenta);

        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaOrigenId(cuentaId);
        transaccion.setMonto(monto);
        transaccion.setTipo("DEPOSITO");
        transaccion.setFecha(LocalDateTime.now());

        return transaccionRepository.save(transaccion);
    }

    @Transactional
    public Transaccion realizarRetiro(Long cuentaId, BigDecimal monto) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El monto no puede ser negativo");
        }
        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
        cuentaRepository.save(cuenta);

        Transaccion transaccion = new Transaccion();
        transaccion.setCuentaOrigenId(cuentaId);
        transaccion.setMonto(monto);
        transaccion.setTipo("RETIRO");
        transaccion.setFecha(LocalDateTime.now());

        return transaccionRepository.save(transaccion);
    }

    @Transactional
    public Transaccion realizarTransferencia(Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal monto) {
        Cuenta cuentaOrigen = cuentaRepository.findById(cuentaOrigenId)
                .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));
        Cuenta cuentaDestino = cuentaRepository.findById(cuentaDestinoId)
                .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));

        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El monto no puede ser negativo");
        }
        if (cuentaOrigen.getSaldo().compareTo(monto) < 0) {
            throw new RuntimeException("Saldo insuficiente en la cuenta origen");
        }


        if (!cuentaOrigen.getMoneda().equals(cuentaDestino.getMoneda())) {
            throw new RuntimeException("Las cuentas deben tener el mismo tipo de moneda para realizar la transferencia");
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

        return transaccionRepository.save(transaccion);
    }

    @Transactional
    public List<Transaccion> obtenerHistorial(Long cuentaId, String tipo, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return transaccionRepository.findByCuentaOrigenIdAndTipoAndFechaBetween(cuentaId, tipo, fechaDesde, fechaHasta);
    }

    @Transactional
    public ReporteFinancieroDTO generarReporteFinanciero(Long cuentaId, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

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

        return reporte;
    }
}
