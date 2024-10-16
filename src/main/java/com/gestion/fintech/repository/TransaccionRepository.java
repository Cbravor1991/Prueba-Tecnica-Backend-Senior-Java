package com.gestion.fintech.repository;

import com.gestion.fintech.model.Transaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    Page<Transaccion> findByCuentaOrigenIdAndTipoAndFechaBetween(Long cuentaId, String tipo, LocalDateTime fechaDesde, LocalDateTime fechaHasta, Pageable pageable);

    @Query("SELECT SUM(t.monto) FROM Transaccion t WHERE t.cuentaOrigenId = :cuentaId AND t.tipo = :tipo AND t.fecha BETWEEN :fechaDesde AND :fechaHasta")
    BigDecimal sumarMontosPorTipo(@Param("cuentaId") Long cuentaId, @Param("tipo") String tipo, @Param("fechaDesde") LocalDateTime fechaDesde, @Param("fechaHasta") LocalDateTime fechaHasta);
}
