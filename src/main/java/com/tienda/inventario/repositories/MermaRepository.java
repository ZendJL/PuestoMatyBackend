package com.tienda.inventario.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.Merma;

public interface MermaRepository extends JpaRepository<Merma, Integer> {

    // Mermas por tipo (EXPIRADO, USO_PERSONAL, MAL_ESTADO...)
    List<Merma> findByTipoMerma(String tipoMerma);

    // Mermas por tipo y rango de fechas (para reportes diario/semanal/mensual)
    List<Merma> findByTipoMermaAndFechaSalidaBetweenOrderByFechaSalidaAsc(
            String tipoMerma, LocalDateTime desde, LocalDateTime hasta);

    // Todas las mermas por rango de fechas
    List<Merma> findByFechaSalidaBetweenOrderByFechaSalidaAsc(
            LocalDateTime desde, LocalDateTime hasta);
}
