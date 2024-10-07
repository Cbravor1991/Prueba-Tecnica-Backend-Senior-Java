package com.gestion.fintech.repository;

import com.gestion.fintech.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    Page<Transaccion> findByCuentaOrigenIdAndTipoAndFechaBetween(Long cuentaId, String tipo, LocalDateTime fechaDesde, LocalDateTime fechaHasta, Pageable pageable);


}
