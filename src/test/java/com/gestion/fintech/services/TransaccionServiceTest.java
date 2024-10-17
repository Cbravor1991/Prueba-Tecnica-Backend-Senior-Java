package com.gestion.fintech.services;
import com.gestion.fintech.exception.TransaccionException;
import com.gestion.fintech.model.Cuenta;
import com.gestion.fintech.model.Transaccion;
import com.gestion.fintech.dto.ReporteFinancieroDTO;
import com.gestion.fintech.repository.CuentaRepository;
import com.gestion.fintech.repository.TransaccionRepository;
import com.gestion.fintech.service.TransaccionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransaccionServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private TransaccionRepository transaccionRepository;

    @InjectMocks
    private TransaccionService transaccionService;

    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setSaldo(BigDecimal.valueOf(1000));
        cuenta.setMoneda("USD");
    }

    @Test
    void obtenerCuentaPorId_CuentaExistente_RetornaCuenta() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        Cuenta resultado = transaccionService.obtenerCuentaPorId(1L);

        assertEquals(cuenta, resultado);
        verify(cuentaRepository).findById(1L);
    }

    @Test
    void obtenerCuentaPorId_CuentaNoExistente_LanzaException() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> transaccionService.obtenerCuentaPorId(1L));

        assertEquals("Cuenta no encontrada", exception.getMessage());
    }

    @Test
    void realizarDeposito_MontoValido_RetornaTransaccion() throws Exception {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaccion resultado = transaccionService.realizarDeposito(1L, BigDecimal.valueOf(200)).get();

        assertEquals("DEPOSITO", resultado.getTipo());
        assertEquals(BigDecimal.valueOf(1200), cuenta.getSaldo());
    }

    @Test
    void realizarDeposito_MontoInvalido_LanzaTransaccionException() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        TransaccionException exception = assertThrows(TransaccionException.class, () -> transaccionService.realizarDeposito(1L, BigDecimal.valueOf(-200)).get());

        assertEquals("El monto debe ser positivo", exception.getMessage());
    }

    @Test
    void realizarRetiro_MontoValido_RetornaTransaccion() throws Exception {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaccion resultado = transaccionService.realizarRetiro(1L, BigDecimal.valueOf(200)).get();

        assertEquals("RETIRO", resultado.getTipo());
        assertEquals(BigDecimal.valueOf(800), cuenta.getSaldo());
    }

    @Test
    void realizarRetiro_SaldoInsuficiente_LanzaTransaccionException() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        TransaccionException exception = assertThrows(TransaccionException.class, () -> transaccionService.realizarRetiro(1L, BigDecimal.valueOf(1200)).get());

        assertEquals("Saldo insuficiente", exception.getMessage());
    }

    @Test
    void realizarTransferencia_MontoValido_RetornaTransaccion() throws Exception {
        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setId(2L);
        cuentaDestino.setSaldo(BigDecimal.valueOf(500));
        cuentaDestino.setMoneda("USD");

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.findById(2L)).thenReturn(Optional.of(cuentaDestino));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaccion resultado = transaccionService.realizarTransferencia(1L, 2L, BigDecimal.valueOf(200)).get();

        assertEquals("TRANSFERENCIA", resultado.getTipo());
        assertEquals(BigDecimal.valueOf(800), cuenta.getSaldo());
        assertEquals(BigDecimal.valueOf(700), cuentaDestino.getSaldo());
    }

    @Test
    void realizarTransferencia_MonedasDistintas_LanzaTransaccionException() {
        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setId(2L);
        cuentaDestino.setSaldo(BigDecimal.valueOf(500));
        cuentaDestino.setMoneda("EUR");

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.findById(2L)).thenReturn(Optional.of(cuentaDestino));

        TransaccionException exception = assertThrows(TransaccionException.class, () -> transaccionService.realizarTransferencia(1L, 2L, BigDecimal.valueOf(200)).get());

        assertEquals("Las cuentas deben tener el mismo tipo de moneda para realizar la transferencia", exception.getMessage());
    }

    @Test
    void obtenerHistorial_RetornaTransacciones() throws Exception {

        Pageable pageable = PageRequest.of(0, 10);


        when(transaccionRepository.findByCuentaOrigenIdAndTipoAndFechaBetween(anyLong(), anyString(), any(), any(), any())).thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));


        CompletableFuture<List<Transaccion>> resultado = transaccionService.obtenerHistorial(1L, "DEPOSITO", LocalDateTime.now().minusDays(1), LocalDateTime.now(), 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.get().isEmpty());
    }


}
