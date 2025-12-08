package com.tienda.inventario.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Reportes de ventas por rango de fechas
    List<Venta> findByFechaBetweenOrderByFechaAsc(Date desde, Date hasta);

    // Ventas de un día (pasas desde=00:00 y hasta=23:59)
    List<Venta> findByFechaBetween(Date desde, Date hasta);

    // Ventas por estado (finalizada, en_proceso, cancelada, etc.)
    List<Venta> findByStatus(String status);
}
