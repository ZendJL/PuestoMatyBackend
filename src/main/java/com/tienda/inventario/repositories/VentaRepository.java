package com.tienda.inventario.repositories;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.Venta;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    // Reportes de ventas por rango de fechas
    List<Venta> findByFechaBetweenOrderByFechaAsc(LocalDateTime desde, LocalDateTime hasta);

    // Ventas de un día (pasas desde=00:00 y hasta=23:59)
    List<Venta> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

    // Ventas por estado (finalizada, en_proceso, cancelada, etc.)
    List<Venta> findByStatus(String status);
}
