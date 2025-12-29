package com.tienda.inventario.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.CompraProducto;

public interface CompraProductoRepository extends JpaRepository<CompraProducto, Integer> {

    List<CompraProducto> findByProductoIdOrderByFechaCompraAsc(Integer productoId);
     // último lote (mayor fecha_compra)
    Optional<CompraProducto> findTopByProductoIdOrderByFechaCompraDesc(Integer productoId);
    
}
