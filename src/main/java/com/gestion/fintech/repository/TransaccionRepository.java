package com.gestion.fintech.repository;

import com.gestion.fintech.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByCuentaOrigenIdAndTipoAndFechaBetween(Long cuentaId, String tipo, LocalDateTime fechaDesde, LocalDateTime fechaHasta);

    List<Transaccion> findByCuentaOrigenIdAndFechaBetween(Long cuentaId, LocalDateTime fechaDesde, LocalDateTime fechaHasta);

}
