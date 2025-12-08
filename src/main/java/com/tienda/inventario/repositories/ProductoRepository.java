package com.tienda.inventario.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Productos con stock > 0
    List<Producto> findByCantidadGreaterThan(Long cantidad);

    // Productos por rango de fecha de última venta
    List<Producto> findByUltimaVentaBetween(Date desde, Date hasta);

    // Productos por rango de fecha de última compra
    List<Producto> findByUltimaCompraBetween(Date desde, Date hasta);
}
