package com.tienda.inventario.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.Merma;

public interface MermaRepository extends JpaRepository<Merma, Integer> {
List<Merma> findByFechaSalidaBetween(LocalDateTime desde, LocalDateTime hasta);
List<Merma> findByTipoMermaAndFechaSalidaBetween(String tipoMerma, LocalDateTime desde, LocalDateTime hasta);

}

