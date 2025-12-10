package com.tienda.inventario.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.inventario.entities.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Productos con stock > 0
    List<Producto> findByCantidadGreaterThan(Integer cantidad);

    // Productos por rango de fecha de última compra
    List<Producto> findByUltimaCompraBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Producto> findByActivoTrue();

    // Precio del producto por id
@Query("select p.precio from Producto p where p.id = :id")
Float findPrecioById(@Param("id") Integer id);


}
