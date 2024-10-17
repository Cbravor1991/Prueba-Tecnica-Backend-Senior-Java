package com.gestion.fintech.services;

import com.gestion.fintech.model.Cuenta;
import com.gestion.fintech.repository.CuentaRepository;
import com.gestion.fintech.service.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CuentaServiceTest {

    @InjectMocks
    private CuentaService cuentaService;

    @Mock
    private CuentaRepository cuentaRepository;

    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cuenta = new Cuenta();
        cuenta.setTitular("Christian Bravo");
        cuenta.setMoneda("USD");
    }

    @Test
    void crearCuenta_shouldReturnCuenta() {
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
        CompletableFuture<Cuenta> cuentaCreadaFuture = cuentaService.crearCuenta(cuenta);
        Cuenta cuentaCreada = cuentaCreadaFuture.join();
        assertNotNull(cuentaCreada);
        assertEquals("Christian Bravo", cuentaCreada.getTitular());
        assertTrue(cuentaCreada.getNumeroCuenta().startsWith("CUENTA-"));
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
    }

    @Test
    void actualizarCuenta_shouldReturnUpdatedCuenta() {
        Long cuentaId = 1L;
        cuenta.setId(cuentaId);
        when(cuentaRepository.findById(cuentaId)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
        CompletableFuture<Cuenta> cuentaActualizadaFuture = cuentaService.actualizarCuenta(cuentaId, cuenta);
        Cuenta cuentaActualizada = cuentaActualizadaFuture.join();
        assertNotNull(cuentaActualizada);
        assertEquals("Christian Bravo", cuentaActualizada.getTitular());
        verify(cuentaRepository, times(1)).findById(cuentaId);
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
    }

    @Test
    void eliminarCuenta_shouldNotThrowException() {
        Long cuentaId = 1L;
        doNothing().when(cuentaRepository).deleteById(cuentaId);
        assertDoesNotThrow(() -> cuentaService.eliminarCuenta(cuentaId));
        verify(cuentaRepository, times(1)).deleteById(cuentaId);
    }

    @Test
    void obtenerCuentaPorId_shouldReturnCuenta() {
        Long cuentaId = 1L;
        cuenta.setId(cuentaId);
        when(cuentaRepository.findById(cuentaId)).thenReturn(Optional.of(cuenta));
        Cuenta cuentaObtenida  = cuentaService.obtenerCuentaPorId(cuentaId);

        assertNotNull(cuentaObtenida);
        assertEquals("Christian Bravo", cuentaObtenida.getTitular());
        verify(cuentaRepository, times(1)).findById(cuentaId);
    }

    @Test
    void obtenerCuentaPorId_shouldThrowException_whenCuentaNotFound() {
        Long cuentaId = 1L;
        when(cuentaRepository.findById(cuentaId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cuentaService.obtenerCuentaPorId(cuentaId);
        });
        assertEquals("Cuenta no encontrada", exception.getMessage());
        verify(cuentaRepository, times(1)).findById(cuentaId);
    }
}
